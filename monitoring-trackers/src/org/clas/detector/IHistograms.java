package org.clas.detector;


import java.util.List;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas12.calib.DetectorShape2D;
import org.root.attr.ColorPalette;
import org.root.basic.EmbeddedCanvas;
import org.root.histogram.H1D;


public interface IHistograms {
	
	public void CreateHistoList();
	public void DetectorSelected(DetectorDescriptor desc, EmbeddedCanvas canvas, List<DetectorCollection<H1D>> detectorComponentsHistos);
	public void SetShapeColor(DetectorShape2D shape, ColorPalette palette, int nProcessed) ;
	
}
