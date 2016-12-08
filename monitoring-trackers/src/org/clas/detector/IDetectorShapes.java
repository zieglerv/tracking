package org.clas.detector;

import java.util.List;

import org.jlab.detector.view.DetectorListener;
import org.jlab.detector.view.DetectorPane2D;

public interface IDetectorShapes {


	public void CreateDetectorShapes(List<DetectorShapeView2D> shapeViews,
			List<DetectorShapeTabView> tabViews, DetectorListener listener);
	
}
