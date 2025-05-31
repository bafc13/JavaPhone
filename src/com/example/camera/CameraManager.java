package com.example.camera;

import java.awt.event.ActionListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javaphone.EventInterfaces.VideoHandler;
import javax.swing.Timer;

public class CameraManager {
    public static int chunkSize = 0;
    private VideoCapture camera;
    private boolean isCameraActive = false;
    public static int StyleCount = 0;
    private Timer timer;

    private List<VideoHandler> listeners = new ArrayList<>();


    public void addListener(VideoHandler to_add)
    {
        listeners.add(to_add);
    }

    public void startCamera() {
        if (!isCameraActive) {
            camera = new VideoCapture(0); // 0 - индекс камеры по умолчанию
            isCameraActive = camera.isOpened();
        }
        timer = new Timer(30, e -> {
            getCurrentFrame();
        });
        timer.start();
    }

    public void stopCamera() {
        if (isCameraActive) {
            camera.release();
            isCameraActive = false;
            timer.stop();
        }
    }

    public BufferedImage getCurrentFrame() {
        if (!isCameraActive) {
            return null;
        }

        Mat frame = new Mat();
        camera.read(frame);

        if (frame.empty()) {
            return null;
        }
        if (StyleCount == 1) {
            Core.bitwise_not(frame, frame);
        } else if (StyleCount == 2) {
            Mat sepiaKernel = new Mat(4, 4, CvType.CV_32F);
            sepiaKernel.put(0, 0,
                    /* R */ 0.393, 0.769, 0.189, 0,
                    /* G */ 0.349, 0.686, 0.168, 0,
                    /* B */ 0.272, 0.534, 0.131, 0,
                    /* A */ 0, 0, 0, 1);

            Mat sepia = new Mat();
            Core.transform(frame, sepia, sepiaKernel);
        } else if (StyleCount == 3) {
            Imgproc.GaussianBlur(frame, frame, new Size(15, 15), 0);
        } else if (StyleCount == 4) {
            Mat edges = new Mat();
            Imgproc.cvtColor(frame, edges, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Canny(edges, edges, 100, 200);

            // Для отображения преобразуем обратно в 3-канальное изображение
            Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);
        } else if (StyleCount == 5) {
            // Размеры для уменьшения разрешения
            int pixelSize = 10;
            Size size = new Size(frame.cols() / pixelSize, frame.rows() / pixelSize);

            Mat temp = new Mat();
            // Уменьшаем разрешение
            Imgproc.resize(frame, temp, size, 0, 0, Imgproc.INTER_LINEAR);
            // Возвращаем обратно к исходному размеру, создавая эффект пикселей
            Imgproc.resize(temp, frame, frame.size(), 0, 0, Imgproc.INTER_NEAREST);
        }
        BufferedImage result = matToBufferedImage(frame);

        for (VideoHandler vh : listeners)
        {
            vh.HandleCameraFrameRecorded(result);
        }

        return result;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
            Mat rgb = new Mat();
            Imgproc.cvtColor(mat, rgb, Imgproc.COLOR_BGR2RGB);
            mat = rgb;
        }

        byte[] buffer = new byte[mat.cols() * mat.rows() * (int) mat.elemSize()];
        mat.get(0, 0, buffer);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), buffer);

        return image;
    }

    public boolean isCameraActive() {
        return isCameraActive;
    }
}
