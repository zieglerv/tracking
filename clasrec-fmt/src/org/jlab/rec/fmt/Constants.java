package org.jlab.rec.fmt;


/**
 * 
 * @author defurne, ziegler
 *
 */
public class Constants {


	public Constants() {

	}
	public static final int FVT_Nlayers = 6;
	public static final int FVT_Nstrips = (int) 1024;// Number of strips
    public static final int FVT_Midstrips = (int) 320; // In the middle of the FMT, 320 strips are splitted in two. 
	// units = cm
	public static int MAX_NB_CROSSES = 30;
	
	public static double FVT_stripwidth = 0.325/10.; //strip width
	public static double FVT_interstrip = 0.2/10.; //inter strip
	public static double FVT_PitchS = FVT_stripwidth+FVT_interstrip;
	public static double FVT_Interlayer = 1.05;
	public static double FVT_Z1stlayer = 30.5219; // z-distance between target center and strips of the first layer.
	public static double FVT_Angle1stlayer = 19*Math.PI/180.;
	public static double FVT_Rmax = FVT_PitchS*(FVT_Nstrips-FVT_Midstrips)/2.;
	public static double FVT_Beamhole = 4.2575;//Radius of the hole in the center for the beam.
    public static double FVT_SigmaS = FVT_PitchS/Math.sqrt(12);
        
    public static double[] FVT_Zlayer = new double[FVT_Nlayers]; //Give z-coordinate of the layer
    public static double[] FVT_Alpha = new double[FVT_Nlayers]; //Give the rotation angle to apply
  
    public static double[][] FVT_stripsXloc = new double[FVT_Nstrips][2]; //Give the local end-points x-coordinates of the strip segment
    public static double[][] FVT_stripsYloc = new double[FVT_Nstrips][2]; //Give the local end-points y-coordinates of the strip segment
    public static double[][][] FVT_stripsX = new double[FVT_Nlayers][FVT_Nstrips][2]; //Give the  end-points x-coordinates of the strip segment rotated in the correct frame for the layer
    public static double[][][] FVT_stripsY = new double[FVT_Nlayers][FVT_Nstrips][2]; //Give the  end-points y-coordinates of the strip segment
   
    public static double[] FVT_stripslength = new double[FVT_Nstrips]; //Give the strip length
	
	public static String FieldConfig = "nominal";
	public static double TORSCALE = -1;
	public static double SOLSCALE = 1;
    
	public static boolean areConstantsLoaded = false;
	
	// ----- cut based cand select
	public static  double phi12cut = 35.; 
	public static  double phi13cut = 35.; 

	public static  double drdzcut =1.5;
	// ----- end cut based cand select
	
    public static synchronized void Load() {
		if (areConstantsLoaded ) return;

		if(Constants.FieldConfig.equalsIgnoreCase("nominal")) {
			
			Constants.TORSCALE = -1;
			Constants.SOLSCALE = 1;
		}
		
		if(Constants.FieldConfig.equalsIgnoreCase("reverse")) {
			
			Constants.TORSCALE = 1;
			Constants.SOLSCALE = 1;
		}
	
		
		for(int i=0;i<FVT_Nlayers;i++) { 
			FVT_Zlayer[i] = FVT_Z1stlayer+i*FVT_Interlayer;
			FVT_Alpha[i] = (double) i*Math.PI/3.+FVT_Angle1stlayer;
			
		}
		
		for(int i=0;i<FVT_Nstrips;i++) { 
			//Give the Y of the middle of the strip
			if (i<512){
				FVT_stripsYloc[i][0]=-FVT_Rmax+(511-i+0.5)*FVT_PitchS;
				FVT_stripsYloc[i][1]=-FVT_Rmax+(511-i+0.5)*FVT_PitchS;
			} else {
				FVT_stripsYloc[i][0]=FVT_Rmax-(1023-i+0.5)*FVT_PitchS;
				FVT_stripsYloc[i][1]=FVT_Rmax-(1023-i+0.5)*FVT_PitchS;
			}
			
			int localRegion = getLocalRegion(i);
			switch(localRegion) {
			case 2: case 4:
				FVT_stripslength[i]=2*FVT_Rmax*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Rmax));
				FVT_stripsXloc[i][0] = -FVT_stripslength[i]/2.;
				FVT_stripsXloc[i][1] =  FVT_stripslength[i]/2.;
				break;
			case 1:
				FVT_stripslength[i]= FVT_Rmax*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Rmax));
				FVT_stripsXloc[i][1] = 0;
				FVT_stripsXloc[i][0] = -FVT_stripslength[i];
				if(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole<1) {
					FVT_stripslength[i]= FVT_Rmax*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Rmax))-FVT_Beamhole*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole));
					FVT_stripsXloc[i][1] = -FVT_Beamhole*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole));
					FVT_stripsXloc[i][0] = -FVT_stripslength[i];
				}
				break;
			case 3:
				FVT_stripslength[i]= FVT_Rmax*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Rmax));
				FVT_stripsXloc[i][0] = 0;
				FVT_stripsXloc[i][1] = FVT_stripslength[i];
				if(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole<1) {
					FVT_stripslength[i]= FVT_Rmax*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Rmax))-FVT_Beamhole*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole));
					FVT_stripsXloc[i][0] = FVT_Beamhole*Math.sin(Math.acos(Math.abs(FVT_stripsYloc[i][0])/FVT_Beamhole));
					FVT_stripsXloc[i][1] = FVT_stripslength[i];
				}
				break;
			}
			for(int j=0;j<FVT_Nlayers;j++) {
				FVT_stripsX[j][i][0] = FVT_stripsXloc[i][0]*Math.cos(FVT_Alpha[j]) + FVT_stripsYloc[i][0]*Math.sin(FVT_Alpha[j]);
				FVT_stripsY[j][i][0] = -FVT_stripsXloc[i][0]*Math.sin(FVT_Alpha[j]) + FVT_stripsYloc[i][0]*Math.cos(FVT_Alpha[j]);
				FVT_stripsX[j][i][1] = FVT_stripsXloc[i][1]*Math.cos(FVT_Alpha[j]) + FVT_stripsYloc[i][1]*Math.sin(FVT_Alpha[j]);
				FVT_stripsY[j][i][1] = -FVT_stripsXloc[i][1]*Math.sin(FVT_Alpha[j]) + FVT_stripsYloc[i][1]*Math.cos(FVT_Alpha[j]);
			}
			
		
			//System.out.println(Constants.getLocalRegion(i)+" strip-1 = "+i+" x' "+FVT_stripsXloc[i][1]+" y' "+FVT_stripsYloc[i][1]+" length "+FVT_stripslength[i]+" FVT_Beamhole "+FVT_Beamhole);
		}
		areConstantsLoaded = true;
		System.out.println("FMT constants loaded!");
	}
	private static int getLocalRegion(int i) {
		// To represent the geometry we divide the barrel micromega disk into 3 regions according to the strip numbering system.
		// Here i = strip_number -1;
		// Region 1 is the region in the negative x part of inner region: the strips range is from   1 to 320  (   0 <= i < 320)
		// Region 2 is the region in the negative y part of outer region: the strips range is from 321 to 512  ( 320 <= i < 512)
		// Region 3 is the region in the positive x part of inner region: the strips range is from 513 to 832  ( 512 <= i < 832)
		// Region 4 is the region in the positive y part of outer region: the strips range is from 833 to 1024 ( 832 <= i < 1024)
		
		int region = 0;
		if(i>=0 && i<320)
			region =1;
		if(i>=320 && i<512)
			region =2;
		if(i>=512 && i<832)
			region =3;
		if(i>=832 && i<1024)
			region =4;
		
		return region;
	}
		
}