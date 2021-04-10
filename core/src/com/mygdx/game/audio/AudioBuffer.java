package com.mygdx.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;

/**
 * This is the audio processing class. It is designed to be run as a separate thread, and therefore
 * implements the Runnable interface. Only one instance of this class should be used at a time.
 *
 * The class processes incoming audio by using a noise floor on the low passed signal. The incoming
 * audio should surpass the noise floor twice in a row before the system detects the user
 * blowing into the microphone.
 *
 * The blowing is detected by analyzing the frequency region from 30 to 180 Hz. Human speech in
 * a room is recorded above this range, while blowing in the microphone from nearby
 * is mostly recorded inside this range.
 */

public class AudioBuffer implements Runnable {
    /* The recording sample rate. */
    public static final int SAMPLE_RATE = 44100;
    /* The FFT window size. */
    public static final int WINDOW_SIZE = 4096;
    /* The recorded low frequencies should surpass this offset to be detected as blowing. */
    public static final double NOISE_FLOOR_OFFSET = 3.2;
    /* The low frequency band (~30-40 Hz). */
    public static final int LOW_BAND = 3;
    /* The high frequency band (~170-180 Hz). */
    public static final int HIGH_BAND = 17;
    /* Calibrate the noise floor by taking the average of 20 sound frames. */
    public static final int NUM_CALIBRATION_FRAMES = 20;

    private FFT fft;
    private AudioRecorder recordingDevice;

    private short[] buffer;
    private double[] re;
    private double[] im;
    private static double[] bins;

    private static boolean blowing;
    private int currentCalibrationFrame;
    private double noiseFloor;
    private int pass;
    private int numBands;

    /* Construct a new AudioBuffer object that maintains its own audio and filter data. */
    public AudioBuffer() {
        recordingDevice = Gdx.audio.newAudioRecorder(SAMPLE_RATE, true);

        buffer = new short[WINDOW_SIZE];
        fft = new FFT(WINDOW_SIZE);
        re = new double[WINDOW_SIZE];
        im = new double[WINDOW_SIZE];
        bins = new double[WINDOW_SIZE / 2];

        blowing = false;
        numBands = HIGH_BAND - LOW_BAND;
        noiseFloor = 0;
        pass = 0;
        currentCalibrationFrame = 0;
    }

    /* Return whether or not the user is actually blowing. */
    public static boolean isBlowing() {
        return blowing;
    }

    /* Perform FFT on the current buffer data. */
    private void performFFT() {
        /* Set the real part equal to the buffer. Set the imaginary part to zero. This is because
        * all samples contain only real information. */
        for (int i = 0; i < WINDOW_SIZE; i++) {
            re[i] = buffer[i];
            im[i] = 0;
        }

        fft.fft(re, im);

        /* Only calculate the size of the relevant bins. */
        for(int i = LOW_BAND; i < HIGH_BAND; i++) {
            bins[i] = Math.log(re[i] * re[i] + im[i] * im[i]);
        }
    }

    /* Actually run the thread separately. */
    @Override
    public void run() {
        while(true) {
            recordingDevice.read(buffer, 0, buffer.length);

            performFFT();

            /* Calibrate the noise floor to the room noise by taking the average energy of the
            low frequencies for a couple of frames. */
            if(currentCalibrationFrame < NUM_CALIBRATION_FRAMES) {
                for(int i = LOW_BAND; i < HIGH_BAND; i++) {
                    noiseFloor += bins[i];
                }

                currentCalibrationFrame++;
            }

            /* Add some power to the noise floor to prevent premature activation. */
            else if(currentCalibrationFrame == NUM_CALIBRATION_FRAMES) {
                noiseFloor = noiseFloor / ((double) (numBands * NUM_CALIBRATION_FRAMES));
                noiseFloor = noiseFloor + NOISE_FLOOR_OFFSET;

                currentCalibrationFrame++;
            }

            /* The noise floor is now calibrated. We can now properly detect the user blowing. */
            else {
                double currentNoise = 0;

                for(int i = LOW_BAND; i < HIGH_BAND; i++) {
                    currentNoise += bins[i];
                }

                currentNoise = currentNoise / ((double) numBands);

                /* Perform two passes to prevent accidental activation. */
                if(currentNoise > noiseFloor) {
                    if(pass == 0) {
                        pass = 1;
                    } else {
                        blowing = true;
                    }
                } else {
                    pass = 0;
                    blowing = false;
                }
            }

            /* Detect whether the thread should stop running. */
            if (Thread.interrupted()) {
                recordingDevice.dispose();

                return;
            }
        }
    }

}
