package com.debugandroid.VideoGallery;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by srujana chodisetty on 11-Sep-17.
 */

public class WavRecorder {
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    short[] audioData;
    private ByteArrayOutputStream bufferOutput;
    private int amplitude;


    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    int[] bufferData;
    int bytesRecorded;
    private byte[] buffer;
    private String output;

    public WavRecorder(String path) {
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        audioData = new short[bufferSize]; // short array that pcm data is put
        // into.
        output = path;

    }

    private String getFilename() {
        return (output);
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    public void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize);
        //
        int i = recorder.getState();

        if (i == 1) {
            recorder.startRecording();
            /*while (isRecording) {
                int result = recorder.read(buffer, 0, buffer.length);
                if (result == AudioRecord.ERROR_INVALID_OPERATION) {
                    throw new RuntimeException();
                } else if (result == AudioRecord.ERROR_BAD_VALUE) {
                    throw new RuntimeException();
                } else {
                    bufferOutput.write(buffer, 0, result);
                    amplitude = (buffer[0] & 0xff) << 8 | buffer[1];
                    amplitude = Math.abs(amplitude);
                }
            }*/
        }
        isRecording = true;



        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {


                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        buffer = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        bufferOutput = new ByteArrayOutputStream();

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;
        if (null != os) {
            //int k=1;
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                //   int result = recorder.read(data, 0, bufferSize);
                // if (result == AudioRecord.ERROR_INVALID_OPERATION) {
                //     throw new RuntimeException();
                // } else if (result == AudioRecord.ERROR_BAD_VALUE) {
                //   throw new RuntimeException();
                //} else {
                //    bufferOutput.write(data, 0,read);
                //Log.d(MainActivity.LOG_TAG, "Amplitude"+data[1]);
                //amplitude = (data[0] & 0xff) << 8 | data[1];
                //Log.d(MainActivity.LOG_TAG, "Amplitude"+amplitude+"value"+k);
                //  amplitude = Math.abs(amplitude);
                // k=k+1;

                //}*/

                if (read > 0) {
                }

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecording() {
        if (null != recorder) {
            isRecording = false;

            int i = recorder.getState();
            if (i == 1) {
                recorder.stop();
            }
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        copyWaveFile(getTempFilename(), getFilename());
        deleteTempFile();
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        // int channels = ((RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) ? 1
        //        : 2);
        int channels =1;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();

            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (((RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) ? 1
                : 2) * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

}