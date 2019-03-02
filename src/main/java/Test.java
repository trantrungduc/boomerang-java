import org.bytedeco.javacv.*;

import java.io.File;
import java.util.*;

public class Test {
    public static void main(String[] arg) throws FrameGrabber.Exception, FrameRecorder.Exception, FrameFilter.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("in.mp4");
        List<Frame> loop = new ArrayList<Frame>();

        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(new File("out.mp4"), grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setFrameRate(grabber.getFrameRate());
        recorder.setSampleRate(grabber.getSampleRate());
        recorder.setFormat(grabber.getFormat());
        recorder.setVideoCodec(grabber.getVideoCodec());
        recorder.setAudioChannels(grabber.getAudioChannels());
        recorder.setVideoQuality(18);

        recorder.start();

        Frame frame;
        int max = (int)Math.round(2*grabber.getFrameRate());
        while ((frame=grabber.grabImage())!=null){
            int current = grabber.getFrameNumber();
            if (current>=max){
                break;
            }else{
                if (current%2==0) {
                    loop.add(frame.clone());
                }
            }

        }
        frame = null;
        grabber.stop();
        grabber.release();
        for (int k = 0; k < 3; k++) {
            for (Frame frame1 : loop) {
                recorder.record(frame1);
            }
            for (int i=loop.size()-1;i>=0;i--){
                recorder.record(loop.get(i));
            }
        }
        loop.clear();loop=null;
        recorder.stop();
        recorder.release();

    }

}
