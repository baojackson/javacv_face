package video;

import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
/*
 * ��������ͷ
 */
public class CameraDemo {
	/*
	 * ��������װ���׼�
	 */
	public void loadFaceImage(String imagepath, Rect face_i, Mat mat, int x_weight, int y_weight)
	{
		Mat logoMat = opencv_imgcodecs.imread(imagepath);
		Mat mask =  opencv_imgcodecs.imread(imagepath, 0);
		/********�����׼�����ʼ����*******/
		int x = face_i.tl().x() + x_weight;
		int y = face_i.tl().y() + y_weight;
	    Mat imageROI = null;
	    if(x+logoMat.cols() < mat.cols() && y+logoMat.rows() < mat.rows())
	    {
	         imageROI = mat.apply(new Rect(x, y, logoMat.cols(), logoMat.rows()));
	   	     logoMat.copyTo(imageROI, mask);  
	    }
	}
	/*
	 * ����ʶ��
	 */
	public void findFace() throws Exception
	{
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		grabber.start();//��ʼ��ȡ����ͷ����
		CanvasFrame canvas = new CanvasFrame("����ͷ");//�½�һ������
		canvas.setSize(600, 400);
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setAlwaysOnTop(true);
		
		/**********����ʶ������*******/
	    CascadeClassifier face_cascade = new CascadeClassifier(
	                "D:\\haarcascade_frontalface_alt.xml");// haarcascade_frontalface_alt.xml
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 /*************ˮӡ����λ��********/
        Point point = new Point(0, 20);
        /**********��ɫ*********/
        Scalar scalar = new Scalar(0, 0, 255, 0);
        Frame frame = null;
        int tiger = 0;//�ڼ����ϻ���
		while(true)
		{
			if(!canvas.isDisplayable())
			{
				//�����Ƿ�ر�
				grabber.stop();//ֹͣץȡ
				System.exit(2);//�˳�
				
			}
			 frame = grabber.grab();
			/*********���ʱ����ʾ��ˮӡ*************/
	    	 Mat mat = converter.convertToMat(frame);
	    	 
	    	 opencv_imgproc.putText(mat, sdf.format(new Date()), point, opencv_imgproc.CV_FONT_ITALIC, 0.8, scalar, 2, 20, false);
	    	 
	    	 Mat videoMatGray = new Mat();
	         cvtColor(mat, videoMatGray, COLOR_BGRA2GRAY);
	         Point p = new Point();
	         RectVector faces = new RectVector();
	         face_cascade.detectMultiScale(videoMatGray, faces);
	       
	         tiger = tiger + 1;
	         if(tiger >= 9)
	         {
	        	 tiger = 1;
	         }
	         /************��N������*****************/
	         for (int i = 0; i < faces.size(); i++) {
	                Rect face_i = faces.get(i);
	                Mat face = new Mat(videoMatGray, face_i);             
	                rectangle(mat, face_i, new Scalar(0, 255, 0, 1));
	                int x = face_i.tl().x();
	                int y = face_i.tl().y(); 
	                System.out.println("������" + face_i.width() + ",����:" + face_i.height());
	                /*********���ͷ��Ҽ�********/
	                
	                /*********����λ��**********/
	                 loadFaceImage("D:\\hu2.png", face_i, mat, 30, face_i.height()/2);
	                /**********����۾�***************/ 
	                 loadFaceImage("D:\\eye.png", face_i, mat, 0, 30);
	                /*********��Ӷ�̬�ϻ���*********/
	                 String tpath = "D:\\tiger\\tige" + tiger + ".png";
	                loadFaceImage(tpath, face_i, mat, face_i.width(), 0);
	         }
	         
	    	frame = converter.convert(mat);
			canvas.showImage(frame);//��ȡ����ͷͼ�񲢷ŵ���������ʾ����ʾ��һ֡ͼ��
		//	Thread.sleep(40);//25֡ͼ��
		}
	}

	public static void main(String[] args) throws Exception, InterruptedException {
	
		CameraDemo camera = new CameraDemo();
		camera.findFace();
	}
		
}
