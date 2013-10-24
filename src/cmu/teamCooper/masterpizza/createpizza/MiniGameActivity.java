package cmu.teamCooper.masterpizza.createpizza;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import processing.core.PApplet;
import android.view.MotionEvent;

public class MiniGameActivity extends PApplet {
	
	final int INVALID_POINTER_ID = -1;
	private HashedList pointList;

	public void setup() {
	  pointList = new HashedList();
	}

	public void draw() {
	  background(18);
	  pointList.drawInfo();
	}

	public boolean surfaceTouchEvent(MotionEvent me) {
		int action = me.getAction(); 
		float x    = me.getX();
		float y    = me.getY();
		int index  = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;	
		int id     = me.getPointerId(index);
		
	    switch (action & MotionEvent.ACTION_MASK) {
	    case MotionEvent.ACTION_DOWN: {
		    pointList.insert(id, x, y);
		    break;
	    }  
	   
        case MotionEvent.ACTION_UP: {
            break;
        }

	    case MotionEvent.ACTION_MOVE: {
	        int numPointers = me.getPointerCount();
	        for (int i=0; i < numPointers; i++) {
	          id = me.getPointerId(i);
	          x  = me.getX(i);
	          y  = me.getY(i);
			  pointList.update(id, x, y);
	        }
		    break;
	    } 
        
	    case MotionEvent.ACTION_POINTER_DOWN: {
		    pointList.insert(id, x, y);
	    	break;
	    }	   
        
        case MotionEvent.ACTION_POINTER_UP: {
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
        	pointList.clearMe();
            id = INVALID_POINTER_ID;
            break;
        }
	    }
	    
	    return super.surfaceTouchEvent(me);
	}
	
	class HashedList {
		private Map<Integer,Points> hashList;

		HashedList () {
			hashList = new HashMap<Integer,Points>();
		}
		
		public synchronized void drawInfo() {
	        Set<Integer> keyList = hashList.keySet();
	        Iterator<Integer> iter = keyList.iterator();
	        int cnt = 0;
	        Points anchor = null;
	        LinkedList<Points> lista = new LinkedList<Points>();
	        while(iter.hasNext()){
	        	anchor = hashList.get(iter.next());
	        	lista.add(anchor);
	        	anchor.drawIt();
	        	cnt++;
	        }
	        
	        textSize(25);
	        text("Active elements: " + cnt,10,25);

	        return;
		}

		synchronized void delete(int id) {
			if ( hashList.get(id) != null )
				hashList.remove(id);
		}
		
		synchronized void clearMe() {
			hashList.clear();
		}

		synchronized void insert(int id, float x, float y) {
			if ( hashList.get(id) == null )
				hashList.put(id, new Points(id,x,y));
		}
		
		synchronized void update(int id, float x, float y) {
			hashList.get(id).update(x, y);
		}
	}
	
	class Points {
		float posX,posY;
		int pointID;
		int textSize = 12;
		
		Points(int id, float x, float y) {
			pointID = id;
			posX    = x;
			posY    = y;
		}
		
		void update(float x, float y) {
			posX = x;
			posY = y;
		}
		
		void drawIt() {
        	fill(120);
        	ellipse(posX,posY,100,100);
		}
	}
	
}
