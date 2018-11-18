package com.example.android.tj;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

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

import static com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID;


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
    private TJService ctx;
    List<String> imageFilesPaths;

    boolean hasStarted = false; // if the player finished loading the 1st resource

    Nodes(TJService ctx) {
        this.ctx = ctx;
        File[] files = new File(TJ_DIR).listFiles();
        nodes = Arrays.stream(files).map(Node::new).collect(Collectors.toCollection
                (LinkedList::new));

        this.imageFilesPaths = Arrays.stream(new File(TJ_DIR_IMG).listFiles()).map(File
                ::getAbsolutePath).collect(Collectors.toList());


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


        //priority sort
        nodes.sort((n1, n2) -> {
                    if (n2.metadata.priority == n1.metadata.priority) {
                        return n1.file.getName().compareTo(n2.file.getName());
                    } else {
                        return n2.metadata.priority - n1.metadata.priority;
                    }
                }
        );


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

            player.setDataSource(ctx, Uri.fromFile(n.file));
            player.prepare();
            player.start();
            player.setOnCompletionListener(finishedPlayer -> {
                try {
                    finishedPlayer.reset();
                    Node n2 = forwardNode();

                    player.setDataSource(ctx, Uri.fromFile(n2.file));
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
        Collections.shuffle(this.imageFilesPaths);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.ctx,
                NOTIFICATION_CHANNEL_ID);

        return notificationBuilder
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(this.ctx.mediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setLargeIcon(BitmapFactory.decodeFile(this.imageFilesPaths.get(0)))
                .setContentTitle(nodes.getLast().metadata.name)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }
}
