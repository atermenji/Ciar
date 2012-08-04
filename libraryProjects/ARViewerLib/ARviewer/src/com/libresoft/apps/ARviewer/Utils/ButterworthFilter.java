package com.libresoft.apps.ARviewer.Utils;

public class ButterworthFilter{
	
//	private static final int NZEROS = 4;
//	private static final int NPOLES = 4;
//	private static final float GAIN = (float) 9.794817390E1;
//	private static final float[] XV_W = {1, 4, 6};
//	private static final float[] YV_W = {-0.1203895999f, 0.7244708295f, -1.7358607092f, 1.9684277869f};
	
//	private static final int NZEROS = 8;
//	private static final int NPOLES = 8;
//	private static final float GAIN = (float) 9.266871417E3;
//	private static final float[] XV_W = {1, 8, 28, 56, 70};
//	private static final float[] YV_W = {-0.0155076153f, 0.1863424777f, -1.0016965796f, 3.1560252607f, 
//		-6.4001540603f, 8.5998150648f, -7.5362341101f, 3.9837842732f};
	
	private static final int NZEROS = 3;
	private static final int NPOLES = 3;
	private static final float GAIN = 8.830127019e+01f;
	private static final float[] XV_W = {1,3};
	private static final float[] YV_W = {0.3464101615f, -1.4000000000f, 1.9629909152f};
	
//  Sobreoscilaciones
//	private static final int NZEROS = 10;
//	private static final int NPOLES = 10;
//	private static final float GAIN = 3.657058509e+07f;
//	private static final float[] XV_W = {1,10,45,120,210,252};
//	private static final float[] YV_W = {-0.0795244105f, 0.9947914379f, -5.6301292691f, 18.9908844679f, -42.2945772469f,
//		65.0115147657f, -69.8812733103f, 51.8964268544f, -25.4988824131f, 7.4907411233f};
	
	private float[] XV = new float[NZEROS + 1];
	private float[] YV = new float[NPOLES + 1];
	
	public float getValue(float new_value){
		
		for(int i = 0; i < NZEROS; i++){
			XV[i] = XV[i + 1];
		}
		XV[NZEROS] = new_value / GAIN;

		for(int i = 0; i < NPOLES; i++){
			YV[i] = YV[i + 1];
		}
		
		float xv_sum = 0;
		for(int i = 0; i<XV_W.length; i++){
			if(i != NZEROS - i)
				xv_sum += XV_W[i] * (XV[i] + XV[NZEROS - i]);
			else
				xv_sum += XV_W[i] * XV[i];
		}
		
		float yv_sum = 0;
		for(int i = 0; i<YV_W.length; i++){
			yv_sum += YV_W[i] * YV[i];
		}
//		YV[NPOLES] = (XV[0] + XV[4]) + 4 * (XV[1] + XV[3]) + 6 * XV[2] + 
//			(  -0.1203895999f * YV[0]) + ( 0.7244708295f * YV[1]) + 
//			(  -1.7358607092f * YV[2]) + (  1.9684277869f * YV[3]);
		YV[NPOLES] = xv_sum + yv_sum;
			
			
		return YV[NPOLES];
	}
	
}