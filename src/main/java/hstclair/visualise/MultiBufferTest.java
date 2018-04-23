package hstclair.visualise;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * This test takes a number up to 13 as an argument (assumes 2 by
 * default) and creates a multiple buffer strategy with the number of
 * buffers given.  This application enters full-screen mode, if available,
 * and flips back and forth between each buffer (each signified by a different
 * color).
 */

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class MultiBufferTest {

    static final int a = 255 << 24;

    private static Color[] COLORS = new Color[] {
            Color.red, Color.blue, Color.green, Color.white, Color.black,
            Color.yellow, Color.gray, Color.cyan, Color.pink, Color.lightGray,
            Color.magenta, Color.orange, Color.darkGray };
    private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
            new DisplayMode(640, 480, 32, 0),
            new DisplayMode(640, 480, 16, 0),
            new DisplayMode(640, 480, 8, 0)
    };

    Frame mainFrame;

    public MultiBufferTest(int numBuffers, GraphicsDevice device) {

        int edgeLength = 128;
        double diffusion = 0.0001;
        double viscosity = .0000003;
        double dt = .03;
        double dConst = .5;
        double timeScale = 1;

        double peakDensity = 0;

        double meanMax = 0;
        long cycles = 0;


        double displayScale = 1024 / edgeLength;

        FluidSolver solver = new FluidSolver(edgeLength, dt, viscosity, diffusion);

        try {
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            device.setFullScreenWindow(mainFrame);
            if (device.isDisplayChangeSupported()) {
                chooseBestDisplayMode(device);
            }

            BufferedImage image = new BufferedImage(solver.edgeLength, solver.edgeLength, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.black);

            g2d.fillRect(0, 0, solver.edgeLength, solver.edgeLength);

            g2d.dispose();


            mainFrame.createBufferStrategy(numBuffers);
            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();

            long nanos;

            while (true) {     // int cycles = 0; cycles < 100; cycles++) {

                for (int i = 0; i < numBuffers; i++) {

                    nanos = System.nanoTime();


                    Graphics g = bufferStrategy.getDrawGraphics();

                    if (!bufferStrategy.contentsLost()) {

                        int step = solver.edgeLength / 1;

                        for (int x = step/2; x < solver.edgeLength; x+=step) {

                            double vConst = .5;
                            double dxConst = 0;
                            double dyConst = 0;

                            double v = vConst;

                            solver.applyForce(x, solver.edgeLength-1, dxConst*v*dt, dyConst*v*dt);
                            solver.applyDensity(x, solver.edgeLength-1, dConst*dt * solver.edgeLength);
                        }

                        solver.tick(dt);

 //                       double densityScaleFactor = Math.sqrt(solver.meanDensity - solver.minDensity);

                        peakDensity = Math.max(peakDensity, solver.maxDensity);

                        meanMax += solver.maxDensity;
                        cycles++;

                        double densityScaleFactor = meanMax / cycles / 2;


                        solver.density.eachInnerColRow(indexor -> image.setRGB(indexor.x, indexor.y, greyscaleToRGB((int) (255 * Math.min(1, Math.max(0, 1 - indexor.get() / densityScaleFactor))))));

//                        for (int x = 0; x < solver.edgeLength; x++) {
//                            for (int y = 0; y < solver.edgeLength; y++) {
//
//                                double density = solver.density.get(x, y);
//
//                                double densityScaled = density / densityScaleFactor;
//
//                                if (1 - densityScaled < 0)
//                                    densityScaled = .99;
//
//                                if (1 - densityScaled > 1)
//                                    densityScaled = 0;
//
//                                image.setRGB(x, y, greyscaleToRGB( (int)(255 * Math.min(1, Math.max(0, 1 - densityScaled)))));
//                            }
//                        }


                        g.drawImage(image, 10, 10, (int) (solver.edgeLength * displayScale), (int) (solver.edgeLength * displayScale), null);

                        bufferStrategy.show();
                        g.dispose();
                    }


//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {}

                    nanos = System.nanoTime() - nanos;

//                    dt = .03;

//                    dt = (((double) nanos) / 1000000000) * timeScale;

                    System.out.printf("%1$d nanos - %2$f dt  \n", nanos, dt);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    int greyscaleToRGB(int level) {


        if (level >= 255)
            return -1;
        else if (level <= 0)
            return 0;

        int rgb;

//        if (level < 48)
//            rgb = a | (level << 16) | (level << 8) | (96);
//        else
            rgb = (255 << 24) | ((level - 1 ) << 16) | ((level - 1) << 8) | level - 1;

//        if (rgb == 0)
//            return 0;

        return rgb;
    }

    private static DisplayMode getBestDisplayMode(GraphicsDevice device) {
        for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
            DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth()
                        && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
                        && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()
                        ) {
                    return BEST_DISPLAY_MODES[x];
                }
            }
        }
        return null;
    }

    public static void chooseBestDisplayMode(GraphicsDevice device) {
        DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }

    public static void main(String[] args) {
        try {
            int numBuffers = 6;
            if (args != null && args.length > 0) {
                numBuffers = Integer.parseInt(args[0]);
                if (numBuffers < 2 || numBuffers > COLORS.length) {
                    System.err.println("Must specify between 2 and "
                            + COLORS.length + " buffers");
                    System.exit(1);
                }
            }
            GraphicsEnvironment env = GraphicsEnvironment.
                    getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            MultiBufferTest test = new MultiBufferTest(numBuffers, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}