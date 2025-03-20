package me;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class Main {
    static JLabel mainText;
    static JTextField input1;
    static JTextField input2;
    static JTextField input3;
    static JTextField input4;
    static JLabel outputText;
    static JLabel outputText2;
    static Long lo;
    static Long hi;
    static RandomSupport.Seed128bit randomSupport;
    static XoroshiroRandomSource xoro;
    static Long decorationSeed;
    static String outputs;
    static ArrayList<Long> decSeeds;

    public static void main(String[] args) {
        JFrame frame = SetUpWindow();
        decSeeds = new ArrayList<>();

        mainText = SetUpText("Input: \n", 5, 5, 400, 20);
        frame.add(mainText);

        input4 = new JTextField();
        input4.setBounds(425, 10, 400, 30);
        frame.add(input4);

        input1 = new JTextField();
        input1.setBounds(10, 40, 400, 30);
        frame.add(input1);

        JButton button1 = new JButton("Submit");
        button1.setBounds(420, 40, 100, 30);
        frame.add(button1);

        button1.addActionListener(e -> UpdateXoros(Long.parseLong(input1.getText())));
        input1.addActionListener(e -> UpdateXoros(Long.parseLong(input1.getText())));

        outputText = SetUpText("lo: \n hi: ", 5, 70, 1110, 60);
        frame.add(outputText);
        outputText2 = SetUpText("Floats: ", 5, 150, 1110, 30);
        frame.add(outputText2);

        JButton button2 = new JButton("Copy");
        button2.setBounds(420, 85, 100, 30);
        frame.add(button2);

        button2.addActionListener(e -> copyToClipboard(lo + "\n" + hi));

        input2 = new JTextField();
        input2.setBounds(10, 120, 190, 30);
        frame.add(input2);
        input3 = new JTextField();
        input3.setBounds(200, 120, 190, 30);
        frame.add(input3);

        JButton button3 = new JButton("Locate");
        button3.setBounds(420, 120, 100, 30);
        frame.add(button3);

        button3.addActionListener(e -> UpdateDecorationSeed(lo, hi, Integer.parseInt(input2.getText()), Integer.parseInt(input3.getText())));

        JButton button4 = new JButton("Copy");
        button4.setBounds(10, 185, 100, 30);
        frame.add(button4);

        button4.addActionListener(e -> copyToClipboard(outputs));

        JButton button5 = new JButton("CUSTOM");
        button5.setBounds(10, 225, 125, 40);
        frame.add(button5);

        button5.addActionListener(e -> {
            try {
                CUSTOM(input4.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void CUSTOM(String filePath) throws IOException {
        System.out.println("Started custom search");
        ArrayList<Long> seedsList = new ArrayList<>();/*extractLongsFromFile(filePath + "/seeds.txt");*/
        Random r = new Random();

        decSeeds.add(7993349225660619373L); // tall cactus
        decSeeds.add(6042227455591550159L); // tall cactus
        decSeeds.add(-1227231590903941175L); // double spawner
        decSeeds.add(9149345487996528486L); // double spawner
        decSeeds.add(-5043559180645493008L); // double spawner
        decSeeds.add(5743552602603087814L); // double spawner

        int index = 0;
        for (Long d : decSeeds)  {
            decSeeds.set(index, d & 0xFFFFFFFFFFFFL);
            index++;
        }

        index = 0;
        while (true) /*for (Long l : seedsList)*/ {
            Long l = r.nextLong();
            if (l % 10000L == 0)
                System.out.println("(" + index + ") Started seed " + l);

            for (int x = -20; x < 21; x++) {
                for (int z = -20; z < 21; z++) {
                    randomSupport = RandomSupport.upgradeSeedTo128bit(l);
                    lo = randomSupport.seedLo();
                    hi = randomSupport.seedHi();

                    xoro = new XoroshiroRandomSource(lo, hi);
                    long i1 = xoro.nextLong() | 1;
                    long i2 = xoro.nextLong() | 1;
                    decorationSeed = x * i1 + z * i2 ^ l;

                    if (decSeeds.contains(decorationSeed & 0xFFFFFFFFFFFFL)) {
                        AddOutput("DUPE FOUND: " + decorationSeed, filePath);

                        ArrayList<Float> floats = new ArrayList<>();
                        randomSupport = RandomSupport.upgradeSeedTo128bit(decorationSeed);
                        lo = randomSupport.seedLo();
                        hi = randomSupport.seedHi();
                        xoro = new XoroshiroRandomSource(lo, hi);

                        int streak = 0;
                        float average = 0;

                        for (int i = 0; i < 400; i++) {
                            floats.add(xoro.nextFloat());
                            if (floats.getLast() < 0.1) {
                                if (streak <= 0) {
                                    streak--;
                                } else {
                                    if (streak > 8) {
                                        AddOutput("Streak of " + streak + " at " + i + " at " + x + ", " + z + " on seed " + l + " with decSeed of " + decorationSeed, filePath);
                                    }
                                    streak = 0;
                                }
                            } else if (floats.getLast() > 0.9) {
                                if (streak >= 0) {
                                    streak++;
                                } else {
                                    if (streak < -8) {
                                        AddOutput("Streak of " + streak + " at " + i + " at " + x + ", " + z + " on seed " + l + " with decSeed of " + decorationSeed, filePath);
                                    }
                                    streak = 0;
                                }
                            } else {
                                if (Math.abs(streak) > 8) {
                                    AddOutput("Streak of " + streak + " at " + i + " at " + x + ", " + z + " on seed " + l + " with decSeed of " + decorationSeed, filePath);
                                }
                                streak = 0;
                            }
                            average += floats.getLast();
                        }

                        if (Math.abs((average / 200) - 1) > 0.375) {
                            AddOutput("Average of " + (average / 100) + " at " + x + ", " + z + " on seed " + l + " with decSeed of " + decorationSeed, filePath);
                        }
                    }
                }
            }
            index++;
        }

        // decSeeds.clear();
    }

    public static void AddOutput(String s, String filePath) {
        //decSeeds.add(decorationSeed);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "/output.txt", true))) {
            writer.write(s);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void UpdateXoros(long WorldSeed) {
        mainText.setText("Input: " + WorldSeed + "\n");

        randomSupport = RandomSupport.upgradeSeedTo128bit(WorldSeed);
        lo = randomSupport.seedLo();
        hi = randomSupport.seedHi();
        outputText.setText("lo: " + lo + "\n hi: " + hi);
    }

    public static void UpdateDecorationSeed(long lo, long hi, int x, int z) {
        xoro = new XoroshiroRandomSource(lo, hi);
        long i1 = xoro.nextLong() | 1;
        long i2 = xoro.nextLong() | 1;
        decorationSeed = x * i1 + z * i2 ^ Long.parseLong(input1.getText());

        StringBuilder s = new StringBuilder("Floats: ");
        outputs = String.valueOf(decorationSeed);
        ArrayList<Float> floats = new ArrayList<>();
        randomSupport = RandomSupport.upgradeSeedTo128bit(decorationSeed);
        lo = randomSupport.seedLo();
        hi = randomSupport.seedHi();
        xoro = new XoroshiroRandomSource(lo, hi);
        for (int i = 0; i < 100; i++) {
            floats.add(xoro.nextFloat());
            s.append(floats.getLast() + " ");
            // outputs += floats.getLast() + "\n";
        }

        outputText2.setText(decorationSeed + ": " + s.toString());
    }

    public static JFrame SetUpWindow() {
        JFrame frame = new JFrame("Xoroshiro128++ Tools");
        frame.setBounds(400, 160, 1120, 760);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(null);
        return frame;
    }

    public static JLabel SetUpText(String s, int x, int y, int width, int height) {
        JLabel mainText = new JLabel(s);
        mainText.setBounds(x, y, width, height); // Reduce height for better positioning
        return mainText;
    }

    public static void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    static ArrayList<Long> extractLongsFromFile(String filePath) throws IOException {
        ArrayList<Long> longList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                longList.add(Long.parseLong(line.trim()));
            }
        }
        return longList;
    }
}
