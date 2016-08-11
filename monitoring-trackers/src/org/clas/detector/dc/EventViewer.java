package org.clas.detector.dc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jlab.clas.detector.DetectorCollection;
import org.jlab.clas.detector.DetectorDescriptor;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.ParticleGenerator;
import org.jlab.clas12.basic.IDetectorModule;
import org.jlab.clas12.basic.IDetectorProcessor;
import org.jlab.clas12.calib.DetectorModulePane;
import org.jlab.clas12.calib.DetectorShape2D;
import org.jlab.clas12.calib.DetectorShapeTabView;
import org.jlab.clas12.calib.DetectorShapeView2D;
import org.jlab.clas12.calib.IDetectorListener;
import org.jlab.clas12.fastmc.CLAS12FastMC;
import org.jlab.clasrec.main.DetectorEventProcessorPane;
import org.jlab.clasrec.utils.ServiceConfiguration;
import org.jlab.data.io.DataEvent;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.io.decode.EvioRawEventDecoder;
import org.jlab.rec.dc.services.HitBasedTracking;
import org.jlab.rec.dc.services.TimeBasedTracking;
import org.root.histogram.H1D;
import org.root.histogram.H2D;
import org.root.pad.TEmbeddedCanvas;

public class EventViewer implements IDetectorProcessor, IDetectorListener, ItemListener, IDetectorModule, ActionListener {

    // Initialize  Rec
	HitBasedTracking reco = new HitBasedTracking();
	TimeBasedTracking reco2 = new TimeBasedTracking();
	
    EvioRawEventDecoder decoder = new EvioRawEventDecoder();
    DCTranslationTable table = new DCTranslationTable();
    DCDecoder deco = new DCDecoder();

    DetectorDrawOnlineRecoComponents displays = new DetectorDrawOnlineRecoComponents();
   
    // Define the panel and views
    DetectorShapeView2D detectorPanel = null;
    List<DetectorShapeView2D> detectorPanel2 = new ArrayList<DetectorShapeView2D>();
    DetectorShapeView2D detectorPanel3 = null;
    DetectorShapeView2D plotsPanel = null;
    //
    DetectorModulePane detectorModulePane = null;

    JLabel groupLabel1 = null;
    JLabel groupLabel2 = null;
    JLabel groupLabel3 = null;
    JLabel groupLabel4 = null;
    JLabel groupLabel5 = null;
    JLabel groupLabel6 = null;

    JFormattedTextField nEventsField;
    JFormattedTextField nSkipEventsField;
    JFormattedTextField displayEventField;

    //
    DetectorDrawComponents detFrm = new DetectorDrawComponents();
    OnlineRecoFillHistograms histos = new OnlineRecoFillHistograms();
    DetectorEventProcessorPane evPane = new DetectorEventProcessorPane();
    TEmbeddedCanvas canvas = new TEmbeddedCanvas();
    DetectorShapeTabView view = new DetectorShapeTabView();
    JFrame frame = new JFrame();

    private List<JComboBox<String>> comboBoxes = new ArrayList<JComboBox<String>>();
//    private List<String> plotName = new ArrayList<String>();
    private long eventNr = 0;
    private List<String> plotName;
   

    public EventViewer() {
        // configure reconstruction
        
        reco.init();
        ServiceConfiguration config = new ServiceConfiguration();
        config.addItem("DAQ", "data", "true");
        config.addItem("DATA", "mc", "false");
        config.addItem("GEOM", "new", "true");

        reco.configure(config);
        reco2.init();
        reco2.configure(config);
        //
        detectorModulePane = new DetectorModulePane(4000, 2000, 1);
        detectorModulePane.addCanvas("Map");
        detectorModulePane.addCanvas("Layer");
        detectorModulePane.addCanvas("Superlayer");
        detectorModulePane.addCanvas("Region");
        detectorModulePane.addCanvas("Track");
        detectorModulePane.addCanvas("Summary");
       
        groupLabel1 = new JLabel("Maps");
        groupLabel2 = new JLabel("Layer");
        groupLabel3 = new JLabel("Superlayer");
        groupLabel4 = new JLabel("Region");
        groupLabel5 = new JLabel("Summary");
        groupLabel6 = new JLabel("Track Properties");

        //
        this.detectorModulePane.getControlPanel().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        
        // configure views	   
        this.plotsPanel = new DetectorShapeView2D("Histograms");
        this.plotsPanel.setLayout(new BorderLayout());
        this.plotsPanel.add(this.canvas);
        histos.CreateHistos();

        this.detectorPanel = new DetectorShapeView2D("DC Transverse View");
        detFrm.CreateViews(this);
        // histos.CreateDetectorShapes(new ArrayList<DetectorCollection<H1D>>());
        this.detectorPanel.setLayout(new BorderLayout());

        for(int s =0; s<6; s++) {
        	DetectorShapeView2D dtSecPanel = new DetectorShapeView2D("DC Sector "+(s+1));
        	this.detectorPanel2.add(s, dtSecPanel);
        	this.detectorPanel2.get(s).setLayout(new BorderLayout());
        }

        this.evPane.addProcessor(this);
        JPanel topView = new JPanel();
        topView.setLayout(new FlowLayout());

        
        JPanel ModView2 = new JPanel();
        ModView2.setLayout(new FlowLayout());

        topView.add(detFrm.get_TabViews().get(0));	 		 	// transverse view
       
        this.detectorPanel.add(topView, BorderLayout.PAGE_START);
       
        for(int s=0; s<6; s++) {
        	JPanel ModView = new JPanel();
            ModView.setLayout(new FlowLayout());
            ModView.add(detFrm.get_TabViews().get(s+1));
        	this.detectorPanel2.get(s).add(ModView, BorderLayout.PAGE_START);
        }
        this.frame.add(this.evPane, BorderLayout.PAGE_END);

        this.detectorModulePane.getDetectorView().addDetectorLayer(detectorPanel);
        for(int s=0; s<6; s++)
        	this.detectorModulePane.getDetectorView().addDetectorLayer(this.detectorPanel2.get(s));
        
        //this.view.addDetectorLayer(this.detectorPanel);

        JComboBox<String> combo = null;
       
        this.detectorModulePane.getControlPanel().add(groupLabel1, c); // occupancy map
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("occupancy");
        combo.addItem(" ");
       
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        c.gridx = 0;
        //Components plots
        this.detectorModulePane.getControlPanel().add(groupLabel2, c); // layer stats
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("hit-based statistics");
        combo.addItem("time-based statistics");
        combo.addItem("hit times");
        combo.addItem("hit docas");
        combo.addItem("hit track docas");      
        combo.addItem("hit-based track local angle (deg.)");
        combo.addItem("time-based track local angle (deg.)");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        //Statistics plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel4, c); // region stats
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("hit-based statistics");
        combo.addItem("time-based statistics");
        combo.addItem("hit-based Cross x vs y (cm)");
        combo.addItem("time-based Cross x vs y (cm)");
        combo.addItem("hit-based Cross ux vs uy (cm)");
        combo.addItem("time-based Cross ux vs uy (cm)");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        //Summary/combined plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel5, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("wire occupancy");
        combo.addItem("channel occupancy");
        combo.addItem("tdc");
        combo.addItem("hit-based statistics");
        combo.addItem("time-based statistics");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
        // tracker object plots
        c.gridx = 0;
        this.detectorModulePane.getControlPanel().add(groupLabel6, c);
        c.gridx++;
        combo = new JComboBox<String>();
        this.detectorModulePane.getControlPanel().add(combo, c);
        combo.addItem("track momentum");
        combo.addItem("track multiplicity");
        combo.addItem("path length");
        combo.addItem("number of hits per track");
        combo.setEditable(true);
        combo.addActionListener(view);
        combo.addItemListener(this);
        comboBoxes.add(combo);
        c.gridy++;
                
        

        this.view.add(detectorModulePane);
        //this.view.addDetectorLayer(this.detectorPanel2);
        // this.view.addDetectorLayer(this.detectorPanel3);
        view.addDetectorListener(this);

        this.plotName = new ArrayList<String>(comboBoxes.size());

        for (int k = 0; k < comboBoxes.size(); k++) {
            this.plotName.add("");
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this.view);
        frame.pack();
        frame.setVisible(true);
       
    }

    @Override
    public void detectorSelected(DetectorDescriptor arg0) {
    	
    	//((int) histos.listOfHistos.get("NbRawHitsPerEvent"));
        //((int) histos.listOfHistos.get("WireOccupancy"));
        //((int) histos.listOfHistos.get("NbRecHBHitsPerEvent"));
        //((int) histos.listOfHistos.get("NbRecTBHitsPerEvent"));
        //((int) histos.listOfHistos.get("CorrectedTimes"));
        //((int) histos.listOfHistos.get("CalcDoca"));
        //((int) histos.listOfHistos.get("TrkDoca"));
        //((int) histos.listOfHistos.get("time_vs_TrkDoca"));
        
      
        
        //((int) histos.listOfHistos.get("NbRecHBHitsInRgPerEvent"));
        //((int) histos.listOfHistos.get("NbRecTBHitsInRgPerEvent"));
        //((int) histos.listOfHistos.get("NbRecHBClustersInRgPerEvent"));
        //((int) histos.listOfHistos.get("NbRecTBClustersInRgPerEvent"));
        //((int) histos.listOfHistos.get("NbRecHBCrossesPerEvent"));
        //((int) histos.listOfHistos.get("NbRecTBCrossesPerEvent"));     
        //((int) histos.listOfHistos.get("y_vs_x"));
        //((int) histos.listOfHistos.get("uy_vs_ux"));
        if (this.plotName.get(1) == "hit-based statistics") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();
            System.out.println("****************************** Layer stats plots -- selecting "+ arg0);
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRawHitsPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBHitsPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("WireOccupancy"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }
        if (this.plotName.get(1) == "hit-based statistics") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();
            System.out.println("****************************** SuperLayer stats plots -- selecting "+ arg0);
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBHitsInSLPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBClustersPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("HBClusterSize"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Superlayer"), DetectorComponentsHistos_LayerTab);
        }
        if (this.plotName.get(1) == "time-based statistics") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecTBHitsInSLPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecTBClustersPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("TBClusterSize"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("SuperLayer"), DetectorComponentsHistos_LayerTab);
        }
         
        if (this.plotName.get(2) == "hit-based statistics") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();
  
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBHitsInRgPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBClustersInRgPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecHBCrossesPerEvent"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Region"), DetectorComponentsHistos_LayerTab);
        }
        if (this.plotName.get(2) == "time-based statistics") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecTBHitsInRgPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecTBClustersInRgPerEvent"))));
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("NbRecTBCrossesPerEvent"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Region"), DetectorComponentsHistos_LayerTab);
        }
        if (this.plotName.get(1) == "hit times") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("CorrectedTimes"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "hit docas") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("CalcDoca"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "hit track docas") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();
 
            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("TrkDoca"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "hit-based track local angle (deg.)") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("HBTrkAngle"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }

        if (this.plotName.get(1) == "time-based track local angle (deg.)") {
            List<DetectorCollection<H1D>> DetectorComponentsHistos_LayerTab = new ArrayList<DetectorCollection<H1D>>();

            DetectorComponentsHistos_LayerTab.add(histos.get_DetectorComponentsHistos().get(((int) histos.listOfHistos.get("TBTrkAngle"))));
            histos.DetectorSelected(arg0, detectorModulePane.getCanvas("Layer"), DetectorComponentsHistos_LayerTab);
        }
///////////////////////////
        
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        for (int i = 0; i < comboBoxes.size(); i++) {
            if (event.getSource() == comboBoxes.get(i)
                    && event.getStateChange() == ItemEvent.SELECTED) {
                this.plotName.add(i, comboBoxes.get(i).getSelectedItem().toString());
                System.out.println(" Selected " + comboBoxes.get(i).getSelectedItem().toString());
            }
        }
    }

    

    @Override
    public void update(DetectorShape2D shape) {
       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       

        
    }

   
    

    @Override
    public String getAuthor() {
        // TODO Auto-generated method stub
        return "ziegler";
    }

    @Override
    public String getDescription() {
        return "Forward Tracker Monitoring";
    }

    @Override
    public JPanel getDetectorPanel() {
        return this.detectorPanel;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "ziegler";
    }

    @Override
    public DetectorType getType() {
        // TODO Auto-generated method stub
        return DetectorType.DC;
    }

    @Override
    public void processEvent(DataEvent de
    ) {
        this.eventNr++;
        EvioDataEvent event = (EvioDataEvent) de;
        EvioDataEvent decodedEvent = deco.DecodeEvent(event, decoder, table);
        //decodedEvent.show();

        reco.processEvent(decodedEvent);
        displays.PlotAllHits(decodedEvent, detFrm.get_ShapeViews(), detFrm.get_TabViews());
        reco2.processEvent(decodedEvent);
        displays.PlotCrosses(decodedEvent, detFrm.get_ShapeViews().get(0), detFrm.get_TabViews().get(0));
        histos.FillHistos(decodedEvent, this.detectorModulePane, this.eventNr, this.plotName);
       
    }

   
    public static void main(String[] args) {
        EventViewer ev = new EventViewer();

        
	
        /*
        frame.add(module.detectorPanel);
        frame.pack();
        frame.setVisible(true);
        JFrame frame2 = new JFrame();
        frame2.add(module.detectorPanel2);
        frame2.pack();
        frame2.setVisible(true);
        JFrame frame3 = new JFrame();
        frame3.add(module.detectorPanel3);
        frame3.pack();
        frame3.setVisible(true);
         */
        //EventViewer module = new EventViewer();
        //    JFrame frame = new JFrame();
        //    frame.add(module.view);
        //    frame.pack();
        //    frame.setVisible(true);
        //JFrame frame4 = new JFrame();
        //frame4.add(module.plotsPanel);
        //frame4.pack();
        //frame4.setVisible(true);
    }

}