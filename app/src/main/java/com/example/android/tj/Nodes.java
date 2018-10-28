package com.example.android.tj;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.widget.ArrayAdapter;

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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
    ArrayAdapter<String> adapter;
    static MediaPlayer player;
    private MainActivity ctx;

    Nodes(MainActivity ctx) {
        this.ctx = ctx;
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
                    n.metadata = ml.getByHash(n.metadata.md5Hash);
                }

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
        adapter = new ArrayAdapter<>(ctx, R.layout.activity_listview, new LinkedList<>());


        player = new MediaPlayer();
    }

    private void resetAdapter() {
        adapter.clear();
        adapter.addAll(IntStream.range(1, nodes.size() + 1).mapToObj(i -> ((Integer) i).toString() +
                ". " +
                nodes.get(i - 1).file.getName()).collect(Collectors.toList()));
        adapter.notifyDataSetChanged();
    }

    private Node forwardNode() {
        Node head = nodes.removeFirst();
        nodes.addLast(head);
        resetAdapter();
        return head;
    }

    private Node backwardNode() {
        Node tail = nodes.removeLast();
        nodes.addFirst(tail);
        resetAdapter();
        return tail;
    }

    void play() {
        player.start();
        Helpers.setSwitch(ctx.switch_, true);
    }

    void pause() {
        player.pause();
        Helpers.setSwitch(ctx.switch_, false);
    }

    void next() {
        this.play(0, true);
    }

    void previous() {
        this.play(0, false);
    }

    void playFromLocation(int loc) {
        Helpers.setSwitch(ctx.switch_, true);
        play(loc, true);
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

            Helpers.setMetadata(ctx, n);

            player.setDataSource(ctx, Uri.fromFile(n.file));
            player.prepare();
            player.start();
            Helpers.setNowPlaying(ctx.nowPlaying, n.file.getName());
            Helpers.setSeekBar(ctx.seekBar, player);
            player.setOnCompletionListener(finishedPlayer -> {
                try {
                    finishedPlayer.reset();
                    Node n2 = forwardNode();
                    player.setDataSource(ctx, Uri.fromFile(n2.file));
                    player.prepare();
                    player.start();
                    Helpers.setNowPlaying(ctx.nowPlaying, n2.file.getName());
                    Helpers.setSeekBar(ctx.seekBar, player);
                    Helpers.setMetadata(ctx, n2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
