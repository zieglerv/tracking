package org.clas.detector;


import java.util.List;

import org.jlab.detector.base.DetectorCollection;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.groot.base.ColorPalette;
import org.jlab.groot.data.H1F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.graphics.EmbeddedPad;


public interface IHistograms {
	
	public void CreateHistoList();
	public void DetectorSelected(DetectorDescriptor desc, EmbeddedCanvas canvas, List<DetectorCollection<H1F>> detectorComponentsHistos);
	public void SetShapeColor(DetectorShape2D shape, ColorPalette palette, int nProcessed) ;
	
}
