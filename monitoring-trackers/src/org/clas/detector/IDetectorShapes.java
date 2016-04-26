package org.clas.detector;

import java.util.List;

import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;

public interface IDetectorShapes {


	public void CreateDetectorShapes(List<DetectorShapeView2D> shapeViews,
			List<DetectorShapeTabView> tabViews, IDetectorListener listener);
	
}
