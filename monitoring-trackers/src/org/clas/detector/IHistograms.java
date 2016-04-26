package org.clas.detector;

import java.util.List;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas12.calib.DetectorShape2D;
import org.root.attr.ColorPalette;
import org.root.histogram.H1D;
import org.root.pad.EmbeddedCanvas;

public interface IHistograms {
	
	public void CreateDetectorShapes(List<DetectorCollection<H1D>> detectorComponentsHistograms);
	public void DetectorSelected(DetectorDescriptor desc, EmbeddedCanvas canvas );
	public void SetShapeColor(DetectorShape2D shape, ColorPalette palette, int nProcessed) ;
	
}
