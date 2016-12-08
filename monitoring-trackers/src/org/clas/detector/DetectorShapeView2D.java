package org.clas.detector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jlab.detector.view.DetectorListener;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Path3D;
import org.jlab.geom.prim.Point3D;
/**
 *
 * @author gavalian
 */
public class DetectorShapeView2D extends JPanel implements MouseListener, MouseMotionListener{
    
    public  Rectangle  drawRegion = new Rectangle();
    private String     canvasName = "undefined";
    private List<DetectorShape2D>   shapes = new ArrayList<DetectorShape2D>();
    private Integer                 selectedShape = -1;
    private ActionListener          listener = null;
    private List<DetectorListener> detectorListeners = new ArrayList<DetectorListener>();
    
    private List<Point3D>           markerPoints      = new ArrayList<Point3D>(); 
    private List<Color>             markerColors      = new ArrayList<Color>(); 
    private List<Path3D>            pathList          = new ArrayList<Path3D>();
    private List<Color>             pathColors        = new ArrayList<Color>();
    
    public boolean MOUSEOVER_CALLBACK = true;
    
    public DetectorShapeView2D(String name){
        canvasName = name;
        addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void setActionListener(ActionListener al){
        this.listener = al;
    }
    
    public void addDetectorListener(DetectorListener lt){
        this.detectorListeners.add(lt);
    }
    
    public String getName(){ return this.canvasName;}
    
    public void addShape(DetectorShape2D shape){
        this.shapes.add(shape);
        this.updateDrawRegion();
    }
    
    public void addPath(Path3D path, int r, int g, int b){
        this.pathList.add(path);
        this.pathColors.add(new Color(r,g,b));
    }
    
    public void addHit(double x, double y, double z, int r, int g, int b){
        this.markerPoints.add(new Point3D(x,y,z));
        this.markerColors.add(new Color(r,g,b));
    }
    
    public void addHit(double x, double y, double z){
        this.markerPoints.add(new Point3D(x,y,z));
        this.markerColors.add(Color.red);
    }
    
    public void clear(){
        this.shapes.clear();
    }
    
    public void clearPaths(){
        this.pathList.clear();
        this.pathColors.clear();
    }
    
    public void clearHits(){
        this.markerPoints.clear();
        this.markerColors.clear();
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int xsize = this.getSize().width;
        int ysize = this.getSize().height;
        Graphics2D g2d = (Graphics2D) g;
        this.draw2D(g2d, 0, 0, xsize, ysize);
    }
    
    public void updateDrawRegion(){
                        
        drawRegion.x = 0;
        drawRegion.y = 0;
        drawRegion.width = 0;
        drawRegion.height = 0;
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        int width  = this.getWidth();
        int height = this.getHeight();
        
        double aspectRatio = ((double) width)/height;
        //System.out.println(" ASPECT RATIO = " + aspectRatio);
        
        for(DetectorShape2D shape : shapes){
            
            int npoints = shape.getShapePath().size();
            for(int p = 0; p < npoints; p++){
                Point3D point = shape.getShapePath().point(p);
                
                if(point.x()>maxX) maxX = point.x();
                if(point.x()<minX) minX = point.x();
                if(point.y()>maxY) maxY = point.y();
                if(point.y()<minY) minY = point.y();
                
                if(point.x()<drawRegion.x) drawRegion.x = (int) point.x();
                if(point.y()<drawRegion.y) drawRegion.y = (int) point.y();
                
                if(point.x()> (drawRegion.x + drawRegion.width)){
                    drawRegion.width = (int) (point.x() - drawRegion.x);
                }
                
                if(point.y()> (drawRegion.y + drawRegion.height)){
                    drawRegion.height = (int) (point.y() - drawRegion.y);
                }
            }
        }
        //System.out.println("UPDATE MINX/MAXX = " + minX + " " + maxX);
        drawRegion.width  = (int) (maxX - minX);
        drawRegion.height = (int) (maxY - minY);
        
        int rw  = (int) ( (double)  drawRegion.width   * 0.1);
        int rh  = (int) ( (double)  drawRegion.height  * 0.1);

        drawRegion.x -= rw;
        drawRegion.y -= rh;
        drawRegion.width  = (int) (drawRegion.width + 2.0*rw);
        drawRegion.height = (int) (drawRegion.height + 2.0*rh);
        
        if(aspectRatio != Double.NaN){
            double size = drawRegion.width;
            if(drawRegion.height>drawRegion.width){
                size = drawRegion.height;
            }
            //drawRegion.height = (int) (size*aspectRatio);
            //drawRegion.width  = (int) (size/aspectRatio);
        }
      
        
    }
    
    public void setColor(int sector, int layer, int component, int r, int g, int b){
        for(DetectorShape2D shape: this.shapes){
            if(shape.getDescriptor().getLayer()==layer &&
                    shape.getDescriptor().getSector()==sector&&
                    shape.getDescriptor().getComponent()==component){
                shape.setColor(r, g, b);
            }
        }
    }
    
    public int getX(float x, int w){        
        double relX = (x - this.drawRegion.x)/this.drawRegion.width;
        return (int) (relX*w);
    }
    
    public int getY(float y, int h){
        double relY = (y - this.drawRegion.y)/this.drawRegion.height;
        return (int) (relY*h);
    }
    
    public List<DetectorShape2D>  getShapes(){
        return this.shapes;
    }
    
    public void draw2D(Graphics2D g2d, int xoff, int yoff, int width, int height){
        
        RenderingHints rh = new RenderingHints(               
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //System.out.println("DRAW : " + width + "  " + height);
        this.updateDrawRegion();
        
        g2d.setRenderingHints(rh);
        
        g2d.setColor(new Color(165,155,155));
        g2d.fillRect(xoff, yoff, width, height);
        int counter = 0;
        
        for(DetectorShape2D shape : shapes){
            
            for(DetectorListener lt : this.detectorListeners){
                lt.processShape(getSelectedShape());
            }
            
            GeneralPath path = new GeneralPath();
            int npoints = shape.getShapePath().size();
            Point3D  startpoint = shape.getShapePath().point(0);
            int startx = this.getX( (float) startpoint.x(), width);
            int starty = this.getY( (float) startpoint.y(), height);
            path.moveTo(startx, starty);
            for(int p = 1; p < npoints; p++){
                Point3D  point = shape.getShapePath().point(p);
                int x = this.getX( (float) point.x(), width);
                int y = this.getY( (float) point.y(), height);
                path.lineTo(x, y);
            }
            path.lineTo(startx, starty);
            
            g2d.setColor(shape.getSwingColor());
            
            if(counter==this.selectedShape){
                g2d.setColor(Color.red);                
            }
            
            g2d.fill(path);
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(Color.BLACK);
            g2d.draw(path);
            
            if(counter==this.selectedShape){
                Font font = new Font(Font.SANS_SERIF,Font.PLAIN,24);
                g2d.setFont(font);
                
            }
            counter++;
        }
        
        
        
        // drawing markers on the Canvas
        g2d.setStroke(new BasicStroke(2));

        int msize = 8;
        for(int index = 0 ; index <  this.markerPoints.size(); index++){
            Point3D p3d = this.markerPoints.get(index);
            Color   p3c = this.markerColors.get(index);
            
            int x = this.getX((int) p3d.x(), width);
            int y = this.getY((int) p3d.y(), height);
            
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x-msize, y-msize, msize*2, msize*2);
            
            g2d.setColor(p3c);
            g2d.drawOval(x-msize, y-msize, msize*2, msize*2);
            g2d.drawLine(x , y-msize,  x, y + msize);
            g2d.drawLine(x-msize, y, x+msize, y );            
        }
        
        g2d.setStroke(new BasicStroke(2));
        for(int index = 0 ; index <  this.pathList.size(); index++){
            Path3D p3d = this.pathList.get(index);
            Color   p3c = this.pathColors.get(index);
            g2d.setColor(p3c);
            for(int p = 0; p < p3d.getNumLines(); p++){
                Line3D line = p3d.getLine(p);
                g2d.drawLine(
                        this.getX( (int) line.origin().x(),width),
                        this.getY( (int) line.origin().y(),height),
                        this.getX( (int) line.end().x(),width),
                        this.getY( (int) line.end().y(),height)
                        );
            }
        }
        
    }
    
    public DetectorShape2D getSelectedShape(){
        if(this.selectedShape>=0) return this.shapes.get(this.selectedShape);
        return null;
    }
    
    public void showDrawRegion(){
        System.out.println(String.format("DRAW REGION X/Y %5d %5d  W/H %6d %6d", 
                this.drawRegion.x,this.drawRegion.y,this.drawRegion.width,this.drawRegion.height));
    }

    public void mouseClicked(MouseEvent e) {  
        double coordinateX = (((double)e.getX())/this.getWidth())*this.drawRegion.width + this.drawRegion.x;
        double coordinateY = (((double) e.getY())/this.getHeight())*this.drawRegion.height + this.drawRegion.y;
        this.showDrawRegion();
        //System.out.println("Mouse clicked " + e.getX() + " x " + e.getY()
        //+ "   REAL WORLD COORDINATES = " + coordinateX + "  " + coordinateY);
        //this.selectedShape = -1;
        int  index = -1;
        for(int loop = 0; loop < this.shapes.size(); loop++){
            if(this.shapes.get(loop).isContained(coordinateX, coordinateY)==true){
                //System.out.println(" SELECTED SHAPE = " + loop);
                index = loop;
                break;
            }
        }
        
        if(index<0){
            this.selectedShape = -1;
            this.repaint();
        }
        
        if(index>=0&&index!=this.selectedShape){
            this.selectedShape = index;
            System.out.println("SHAPE SELECTION HAS CHANGED TO " + index);
            this.repaint();
            for(DetectorListener lt : this.detectorListeners){
               // lt.detectorSelected(this.shapes.get(this.selectedShape).getDescriptor());
            	lt.processShape(getSelectedShape());
            }
            
        }
    }

    public void mousePressed(MouseEvent e) {    }

    public void mouseReleased(MouseEvent e) {    }

    public void mouseEntered(MouseEvent e) {    }

    public void mouseExited(MouseEvent e) {    }

    public void mouseDragged(MouseEvent e) {    }

    
    private void callbackMouseCoordinates(double mX, double mY){
        int index = -1;
        for(int loop = 0; loop < this.shapes.size(); loop++){
                if(this.shapes.get(loop).isContained(mX, mY)==true){
                    //System.out.println(" SELECTED SHAPE = " + loop);
                    index = loop;
                    break;
                }
        }
        if(index<0) this.selectedShape = -1;
        
    }
    
    public void mouseMoved(MouseEvent e) {
        if(this.MOUSEOVER_CALLBACK==true){
            double coordinateX = (((double)e.getX())/this.getWidth())*this.drawRegion.width + this.drawRegion.x;
            double coordinateY = (((double) e.getY())/this.getHeight())*this.drawRegion.height + this.drawRegion.y;
            this.callbackMouseCoordinates(coordinateX, coordinateY);
        }           
    }
}
