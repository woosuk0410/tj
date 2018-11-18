package com.example.android.tj;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


class Nodes {
    class Node {
        File file;
        Metadata metadata;

        Node(File file) {
            this.file = file;

            try {
                this.metadata = new Metadata();
                this.metadata.md5Hash = new String(Hex.encodeHex(DigestUtils.md5(Files
                        .readAllBytes(this.file.toPath()))));
                this.metadata.name = this.file.getName();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static String EXT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String TJ_DIR = EXT_DIR + "/tj";
    private static String TJ_DIR_IMG = EXT_DIR + "/tj_img";
    private static String METADATA_FILE_PATH = EXT_DIR + "/tj.json";

    LinkedList<Node> nodes;
    static MediaPlayer player;
    private TJService tjService;
    private TJNotification tjNotification;

    boolean hasStarted = false; // if the player finished loading the 1st resource

    Nodes(TJService ctx) {
        this.tjService = ctx;
        this.tjNotification = new TJNotification(this, ctx);

        File[] files = new File(TJ_DIR).listFiles();
        nodes = Arrays.stream(files).map(Node::new).collect(Collectors.toCollection
                (LinkedList::new));

        //read from/write to metadata
        try {
            File metadtaFile = new File(METADATA_FILE_PATH);
            if (metadtaFile.exists()) {
                String jsonStr = new String(Files.readAllBytes(Paths.get(METADATA_FILE_PATH)),
                        "UTF-8");

                MetadataList ml = MetadataList.fromJson(jsonStr);

                //match with existing nodes
                for (Node n : nodes) {
                    Optional<Metadata> metadataOp = ml.getByHash(n.metadata.md5Hash);
                    metadataOp.ifPresent(metadata -> n.metadata = metadata);
                    if (!metadataOp.isPresent()) {
                        ml.metadataList.add(n.metadata);
                    }
                }
                FileOutputStream fos = new FileOutputStream(metadtaFile);
                fos.write(ml.toString().getBytes("UTF-8"));
                fos.close();

            } else {
                List<Metadata> list = Arrays.stream(nodes.toArray()).map(node -> ((Node) node)
                        .metadata).collect(Collectors.toList());
                MetadataList ml = new MetadataList();
                ml.metadataList = list;
                String jsonStr = ml.toString();
                FileOutputStream fos = new FileOutputStream(metadtaFile);
                fos.write(jsonStr.getBytes("UTF-8"));
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.priorityShuffle();


        player = new MediaPlayer();


    }

    private Node forwardNode() {
        Node head = nodes.removeFirst();
        nodes.addLast(head);
        return head;
    }

    private Node backwardNode() {
        Node tail = nodes.removeLast();
        nodes.addFirst(tail);
        return tail;
    }

    void play() {
        player.start();
    }

    void pause() {
        player.pause();
    }

    void next() {
        this.play(0, true);
    }

    void previous() {
        this.play(0, false);
    }

    void playFromLocation(int loc) {
        play(loc, true);
    }

    void shuffle() {
        Collections.shuffle(nodes);
    }

    void priorityShuffle() {
        SortedSet<Integer> sortedKeys = new TreeSet<>((i1, i2) -> i2 - i1);
        sortedKeys.addAll(Arrays.stream(nodes.toArray()).map((n -> ((Node) n).metadata.priority))
                .collect(Collectors.toSet()));
        Map<Integer, List<Node>> priorityToNodes = nodes.stream().collect(Collectors.groupingBy(n
                -> n.metadata.priority));
        nodes.clear();
        for (int key : sortedKeys) {
            List<Node> partial = priorityToNodes.get(key);
            Collections.shuffle(partial);
            nodes.addAll(partial);
        }
    }

    private Node currentNode() {
        return nodes.getLast();
    }

    File currentFile() {
        return currentNode().file;
    }

    private void play(int startIdx, boolean forward) {
        try {
            for (int i = 0; i < startIdx; i++) {
                if (forward) forwardNode();
                else backwardNode();
            }

            //backward needs two more steps
            if (!forward) {
                backwardNode();
                backwardNode();
            }

            player.reset();
            Node n = forwardNode();

            player.setDataSource(tjService, Uri.fromFile(n.file));
            player.prepare();
            player.start();
            player.setOnCompletionListener(finishedPlayer -> {
                try {
                    finishedPlayer.reset();
                    Node n2 = forwardNode();

                    player.setDataSource(tjService, Uri.fromFile(n2.file));
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //TODO: may have a better way
            if (!hasStarted) {
                hasStarted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Notification getNotification() {
        return tjNotification.getNotification();
    }

    Bitmap getBitMap() {
        Bitmap bitmap = BitmapFactory.decodeFile(TJ_DIR_IMG + "/tj2.png");

        String hash = currentNode().metadata.md5Hash;
        int curPos = Nodes.player.getCurrentPosition();
        @SuppressLint("DefaultLocale")
        String frameFile = String.format("%s-%03d.jpg", hash, curPos / 1000 / 5 + 1);
        String fullPath = TJ_DIR_IMG + "/" + frameFile;
        File f = new File(fullPath);
        if (f.exists()) {
            bitmap = BitmapFactory.decodeFile(fullPath);
        }
        return bitmap;
    }
}
