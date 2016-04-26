package org.clas.detector.cvt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.clas.detector.IHistograms;
import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.root.attr.ColorPalette;
import org.root.histogram.H1D;
import org.root.pad.EmbeddedCanvas;

public class OnlineRecoFillHistograms implements IHistograms {

	public OnlineRecoFillHistograms() {
		// TODO Auto-generated constructor stub
	}

	public List<DetectorCollection<H1D>> get_DetectorComponentsHistos() {
		return _DetectorComponentsHistos;
	}

	public void set_DetectorComponentsHistos(List<DetectorCollection<H1D>> _DetectorComponentsHistos) {
		this._DetectorComponentsHistos = _DetectorComponentsHistos;
	}

	public List<H1D> get_Histograms() {
		return _Histograms;
	}

	public void set_Histograms(List<H1D> _Histograms) {
		this._Histograms = _Histograms;
	}

	private List<DetectorCollection<H1D>> _DetectorComponentsHistos;	
	private List<H1D> _Histograms;
	
	@Override
	public void CreateDetectorShapes(List<DetectorCollection<H1D>> detectorComponentsHistos) {
		
		for(int regionIdx =0; regionIdx< 4; regionIdx++)
			for(int sectorIdx = 0; sectorIdx < org.jlab.rec.cvt.svt.Constants.NSECT[2*regionIdx+1]; sectorIdx++) 
				for(int sly =0; sly<2; sly++) {
					DetectorCollection<H1D>  dC0 = new DetectorCollection<H1D>();
					DetectorCollection<H1D>  dC1 = new DetectorCollection<H1D>();
					DetectorCollection<H1D>  dC2 = new DetectorCollection<H1D>();
					
					dC0.add(sectorIdx, 2*regionIdx+sly, 0,
							new H1D(DetectorDescriptor.getName("NumberOfHits", sectorIdx, 2*regionIdx+sly,0),
									100, 0, 100));	
					
					dC1.add(sectorIdx, 2*regionIdx+sly, 0,
							new H1D(DetectorDescriptor.getName("NumberOfCluster", sectorIdx, 2*regionIdx+sly,0),
									100, 0, 100));	
					dC2.add(sectorIdx, regionIdx, 0,
							new H1D(DetectorDescriptor.getName("NumberOfCrosses", sectorIdx, regionIdx,0),
									20, 0, 20)); // whole module
					

					detectorComponentsHistos.add(dC0);	
					detectorComponentsHistos.add(dC1);						
					detectorComponentsHistos.add(dC2);	
					
			}
		
	}
	
	public void CreateHistos() {
		List<DetectorCollection<H1D>> detectorComponentsHistos = new ArrayList<DetectorCollection<H1D>>();
		detectorComponentsHistos.add(0, new DetectorCollection<H1D>());
		detectorComponentsHistos.add(1, new DetectorCollection<H1D>());
		detectorComponentsHistos.add(2, new DetectorCollection<H1D>());
		
		this.set_DetectorComponentsHistos(detectorComponentsHistos);
		
		List<H1D> summaryHistograms = new ArrayList<H1D>(4);
		summaryHistograms.add(new H1D("NumberOfHits", 100, 0, 100));
		summaryHistograms.add(new H1D("NumberOfClusters", 100, 0, 100));
		summaryHistograms.add(new H1D("NumberOfCrosses", 20, 0, 20));
		summaryHistograms.add(new H1D("NumberOfTracks", 10, 0, 10));
		
		
		this.set_Histograms(summaryHistograms);
				
		this.CreateDetectorShapes(detectorComponentsHistos) ;		
		
	}
	
	@Override
	/**
     * When the detector is clicked, this function is called
     * @param desc the descriptor
     */
	public void DetectorSelected(DetectorDescriptor desc, EmbeddedCanvas canvas) {
		 canvas.divide(1,3);
	        if(_DetectorComponentsHistos.get(0).hasEntry(desc.getSector(),desc.getLayer(),0)){ // nb hits
	            H1D h1 = _DetectorComponentsHistos.get(0).get(desc.getSector(),desc.getLayer(),0);
	            h1.setTitle(h1.getName());
	            canvas.cd(0);
	            canvas.draw(h1);
	        }
	        if(_DetectorComponentsHistos.get(1).hasEntry(desc.getSector(),desc.getLayer(),0)){ // nb clusters
	            H1D h1 = _DetectorComponentsHistos.get(0).get(desc.getSector(),desc.getLayer(),0);
	            h1.setTitle(h1.getName());
	            canvas.cd(1);
	            canvas.draw(h1);
	        }
	        if(_DetectorComponentsHistos.get(2).hasEntry(desc.getSector(),desc.getLayer(),0)){ // nb crosses
	            H1D h1 = _DetectorComponentsHistos.get(0).get(desc.getSector(),desc.getLayer(),0);
	            h1.setTitle(h1.getName());
	            canvas.cd(2);
	            canvas.draw(h1);
	        }	
	}

	@Override
	public void SetShapeColor(DetectorShape2D shape, ColorPalette palette, int nProcessed) {
		if(_DetectorComponentsHistos.get(0).hasEntry(shape.getDescriptor().getSector(),shape.getDescriptor().getLayer(),0)){ 
			int nent = _DetectorComponentsHistos.get(0).get(shape.getDescriptor().getSector(),shape.getDescriptor().getLayer(),0).getEntries();
			Color col = palette.getColor3D(nent, nProcessed, true);
			shape.setColor(col.getRed(),col.getGreen(),col.getBlue());
		}
	}
	
	public void FillHistos(EvioDataEvent event) {
		 if(event.hasBank("BSTRec::Hits")==true){
			 double[][] array = new double[24][8];
	            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Hits");
	            int rows = bank.rows();
	            for(int loop = 0; loop < rows; loop++){
	                int sector = bank.getInt("sector", loop) ;
	                int layer  = bank.getInt("layer", loop) ;
	                array[sector-1][layer-1]++;
	            }
	            for(int i = 0; i<24; i++)
	            	for(int j = 0; j<8; j++)
	            		if(array[i][j]>0)
	            			_DetectorComponentsHistos.get(0).get(i,j,0).fill(array[i][j]);
		 }
		 if(event.hasBank("BSTRec::Clusters")==true){
			 double[][] array = new double[24][8];
	            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Clusters");
	            int rows = bank.rows();
	            for(int loop = 0; loop < rows; loop++){
	                int sector = bank.getInt("sector", loop) ;
	                int layer  = bank.getInt("layer", loop) ;
	                array[sector-1][layer-1]++;
	            }
	            for(int i = 0; i<24; i++)
	            	for(int j = 0; j<8; j++)
	            		if(array[i][j]>0)
	            			_DetectorComponentsHistos.get(1).get(i,j,0).fill(array[i][j]);
		 }
		 if(event.hasBank("BSTRec::Crosses")==true){
			 double[][] array = new double[24][8];
	            EvioDataBank bank = (EvioDataBank) event.getBank("BSTRec::Crosses");
	            int rows = bank.rows();
	            for(int loop = 0; loop < rows; loop++){
	                int sector = bank.getInt("sector", loop) ;
	                int layer  = bank.getInt("region", loop) ;
	                array[sector-1][layer-1]++;
	            }
	            for(int i = 0; i<24; i++)
	            	for(int j = 0; j<8; j++)
	            		if(array[i][j]>0)
	            			_DetectorComponentsHistos.get(2).get(i,j,0).fill(array[i][j]);
		 }
	}

}
