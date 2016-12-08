package org.clas.detector;

import java.awt.BorderLayout;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.jlab.groot.graphics.EmbeddedCanvas;


/**
 *
 * @author gavalian
 */
public class DetectorModulePane extends JPanel {
    private TreeMap<String,EmbeddedCanvas>  paneCanvas = new TreeMap<String,EmbeddedCanvas>();
    JTabbedPane canvasTabbedPane;
    private JSplitPane    verticalSplitPane   = null;
    private JSplitPane    horizontalSplitPane = null;
    private JPanel        controlsPanel       = null;
    DetectorShapeTabView  detectorView        = null;
    
    public DetectorModulePane(int xsize, int ysize, int mode){
        super();
        this.setSize(xsize, ysize);
        this.setLayout(new BorderLayout());
        canvasTabbedPane = new JTabbedPane();   
        
        this.verticalSplitPane = new JSplitPane();
        //this.add(canvasTabbedPane,BorderLayout.CENTER);
        this.verticalSplitPane.setRightComponent(this.canvasTabbedPane);
        
        
        this.horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.detectorView = new DetectorShapeTabView();
        this.horizontalSplitPane.setTopComponent(this.detectorView);
        this.controlsPanel = new JPanel();
        this.controlsPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        this.controlsPanel.setSize(500,400);
        this.horizontalSplitPane.setBottomComponent(this.controlsPanel);
        this.verticalSplitPane.setLeftComponent(this.horizontalSplitPane);
        this.add(this.verticalSplitPane,BorderLayout.CENTER);
    }
    
    public void addCanvas(String name){
        EmbeddedCanvas canvas = new EmbeddedCanvas();
        this.paneCanvas.put(name, canvas);
        this.canvasTabbedPane.add(name,canvas);
    }
    
    public EmbeddedCanvas getCanvas(String name){
        return this.paneCanvas.get(name);
    }
    
    public DetectorShapeTabView  getDetectorView(){
        return this.detectorView;
    }
    
    public JPanel getControlPanel(){
        return this.controlsPanel;
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        DetectorModulePane pane = new DetectorModulePane(600,600,1);
        pane.addCanvas("Test");
        pane.addCanvas("New Canvas 2");
        pane.addCanvas("New Canvas 3");
        frame.add(pane);
        frame.pack();
        frame.setVisible(true);
    }
}
