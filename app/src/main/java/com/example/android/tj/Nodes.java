package com.example.android.tj;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Nodes {
    class Node {
        File file;

        Node(File file) {
            this.file = file;
        }
    }

    private static String TJ_DIR = "/tj";
    LinkedList<Node> nodes;
    ArrayAdapter<String> adapter;
    static MediaPlayer player;
    private MainActivity ctx;

    Nodes(MainActivity ctx) {
        this.ctx = ctx;
        File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/tj").listFiles();
        nodes = Arrays.stream(files).map(Node::new).collect(Collectors.toCollection
                (LinkedList::new));
        nodes.sort(Comparator.comparing(n -> n.file.getName()));
        adapter = new ArrayAdapter<>(ctx, R.layout.activity_listview, new LinkedList<>());

        player = new MediaPlayer();
    }

    private Node next() {
        Node head = nodes.remove();
        nodes.add(head);
        adapter.clear();
        adapter.addAll(IntStream.range(1, nodes.size() + 1).mapToObj(i -> ((Integer) i).toString() +
                ". " +
                nodes.get(i - 1).file.getName()).collect(Collectors.toList()));
        adapter.notifyDataSetChanged();
        return head;
    }

    void play(int startIdx) {
        try {

            for (int i = 0; i < startIdx; i++) {
                next();
            }

            player.reset();
            Node n = next();
            player.setDataSource(ctx, Uri.fromFile(n.file));
            player.prepare();
            player.start();
            Helpers.setNowPlaying(ctx.nowPlaying, n.file.getName());
            Helpers.setSeekBar(ctx.seekBar, player);
            player.setOnCompletionListener(finishedPlayer -> {
                try {
                    finishedPlayer.reset();
                    Node n2 = next();
                    player.setDataSource(ctx, Uri.fromFile(n2.file));
                    player.prepare();
                    player.start();
                    Helpers.setNowPlaying(ctx.nowPlaying, n2.file.getName());
                    Helpers.setSeekBar(ctx.seekBar, player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
