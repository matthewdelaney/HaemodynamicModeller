/**
 * Copyright 2018 Matthew Delaney
 * 
 * This file is part of HaemodynamicModeller.
 *
 * HaemodynamicModeller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HaemodynamicModeller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HaemodynamicModeller.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Filename: HexagonalGrid.java
 * Purpose : Graphics routines, logic and main program
 * Date    : 8/3/2018
 * @author Matthew Delaney
 */
package hexagonalgrid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class HexagonalGrid extends JFrame {
	private int iHeight;
	private int iWidth;
	private Canvas mainCanvas;
	private int[][] grid;
	private int iMode;
	private int[][] oldGrid;
	private int numCycles = 0;
	
	private int[] collisionCount;
	
	private static final int CELL_WIDTH = 10;
	private static final int CELL_HEIGHT = 10;
	private static final int MACRO_CELL_SIZE = 3;
	private static final int MAGNIFICATION_FACTOR = 1;
	
//	private final double step = Math.PI / 2; // For Mode 4
	private final double step = 2 * Math.PI / 6;
	
	private ArrayList<BoundaryPoint> boundaryPoints;

	private Vector[] c;
	
	public HexagonalGrid(int height, int width, int mode) {
		super("Haemodynamic Modeller v1.0");
		iHeight = height;
		iWidth = width;
		iMode = mode;
		grid = new int[width][height];
		boundaryPoints = new ArrayList<BoundaryPoint>();
		double offset = 4*(Math.PI / 3);

		c = new Vector[iMode];
		
		// TODO: This uses iMode but really only works for mode 6 at the moment
		for(int x = 0; x < iMode; x++) {
			c[x] = new Vector();
			c[x].setX(Math.cos((Math.PI/3)*(x + offset)));
			c[x].setY(Math.sin((Math.PI/3)*(x + offset)));
		}
				
		setupCells();
		this.setSize(1000, 725);
		mainCanvas = new Canvas();
		this.getContentPane().add(mainCanvas);
		collisionCount = new int[64];
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		mainCanvas.createBufferStrategy(2);
		BufferStrategy mainCanvasBS = mainCanvas.getBufferStrategy();

		while(true) {
		  updateGrid();
		  numCycles++;

		  render(mainCanvasBS.getDrawGraphics());
  		  mainCanvasBS.show();
  		  
  		  try {
  			  Thread.sleep(100);
  		  }
  		  catch(InterruptedException e) {
  		  }
		}		
	}
	
	private void updateGrid() {
		oldGrid = new int[iWidth][iHeight];
		
		for(int i = 0; i < iWidth; i++) {
			for(int j = 0; j < iHeight; j++) {
				oldGrid[i][j] = grid[i][j];
			}
		}
		
		for(int i = 0; i < iWidth; i++) {
			for(int j = 0; j < iHeight; j++) {
				if (iMode == 6) {
					update6b(i, j);
				}
				else if (iMode == 4) {
					update(i, j);
				}
			}
		}
	}
	
	private int hexOffset(int j) {
		return ((j == 0 || j % 2 == 0)?1:0);
	}

	private void setupCells() {
		Random rand = new Random();

//		grid[5][5] = 1;

//		Test for type 12 collision
/*		grid[31][30] = 8;
		grid[41][30] = 4; */
		
//		Test for type 38 collision
/*		grid[30][30] = 2;
		grid[41][23] = 4;
		grid[30][16] = 32; */

//		Test for type 14 collision
/*		grid[31][30] = 8;
		grid[41][30] = 4;
		grid[34][35] = 2; */

//		Test for type 30 collision
/*		grid[30][30] = 16;
		grid[23][44] = 2;
		grid[20][37] = 8;
		grid[34][37] = 4; */
//		grid[]
		
//		Test for particle-stream collision
/*	for(int i = 0; i < iWidth-(iWidth-34); i++) {
		for(int j = iHeight/8 + 10; j < (iHeight/4)*2; j++) {
			grid[i][j] = 4; // 2 for Mode 4
		}
	}
	for(int i = (iWidth/4)*2; i < (iWidth/4)*2 + 8; i++) {
		for(int j = iHeight/8 + 10; j < iHeight/8 + 12; j++) {
			grid[i][j] = 32; // 8 for Mode 4
		}
	} */
		
//		Test for solid-body collision
/*		for(int i = 10; i < 11; i++) {
			for(int j = iHeight/8 + 10; j < iHeight/8 + 11; j++) {
				grid[i][j] = 4;
			}
		}
		for(int i = (iWidth/4)*2; i < (iWidth/4)*2+1; i++) {
			for(int j = iHeight/8 + 10; j < iHeight/8 + 11; j++) {
				grid[i][j] = 64;
			}
		} */
		
/*		grid[50][15] = 64;

		grid[40][15] = 4;
		grid[45][5] = 8;
		grid[60][15] = 32;
		grid[55][5] = 16;
		grid[45][25] = 2;
		grid[55][25] = 1; */
		
		for(int q = 0; q < 10; q++) {
			for(int w = 0; w < 10; w++) {
				boundaryPoints.add(new BoundaryPoint(40+q, 15+w)); // 40+q, 15+w
//				grid[40+q][15+w] = 64;
			}
		}
		
		for(int a = 0; a < iWidth; a++) {
			boundaryPoints.add(new BoundaryPoint(a, 0));
			boundaryPoints.add(new BoundaryPoint(a, iHeight-1));
		}
		
		for(int e = 0; e < 20; e++) {
			for(int w = 5; w < 30; w++) {
				grid[e][w] = 4;
			}
		}
		
// Test for graphics system
/*	for(int i = 0; i < iWidth; i++) {
		for(int j = iHeight/8; j < (iHeight/2)*2; j++) {
			grid[i][j] = 1;
		}
	 } */
  }


	private void update(int i, int j) {
		int controlVector = oldGrid[i][j];
		int backup = 0;

		
			switch(controlVector) {
			case 0: grid[(i-1 >=0)?i-1:iWidth-1][j] &= 7;
			 		grid[i][(j-1 >= 0)?j-1:iHeight-1] &= 11;
			 		grid[(i+1 < iWidth)?i+1:0][j] &= 13;
			 		grid[i][(j+1 < iHeight)?j+1:0] &= 14;
			 		break;
			case 1: grid[i][(j+1 < iHeight)?j+1:0] |= 1;
					break;
			case 2: or(i+1, j, get(i+1, j, false) | 2);
					break;
			case 4: or(i, j - 1, get(i, j - 1, false) | 4);
					break;
			case 5: or(i-1, j, get(i-1, j, false) | 8); 
					or(i+1, j, get(i+1, j, false) | 2);
					break;
			case 8: grid[(i-1 >=0)?i-1:iWidth-1][j] |= 8;
					break;
			case 10: grid[i][(j-1 >= 0)?j-1:iHeight-1] |= 4;
					 grid[i][(j+1 < iHeight)?j+1:0] |= 1;
					 break;
			default: grid[(i-1 >=0)?i-1:iWidth-1][j] |= (controlVector & 8);
					 grid[i][(j-1 >= 0)?j-1:iHeight-1] |= (controlVector & 4);
					 grid[(i+1 < iWidth)?i+1:0][j] |= (controlVector & 2);
					 grid[i][(j+1 < iHeight)?j+1:0] |= (controlVector & 1);
					 break;
			}			
	}
	

	private void update6b(int i, int j) {
		int controlVector = oldGrid[i][j];
		int backup = 0;
		Random rand = new Random();

		if (controlVector > 64) {
			System.out.println("Anomalous control vector: " + controlVector);
		}
		
			switch(controlVector) {
			case 64:
			case 0:
					grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j-1 >= 0)?j-1:iHeight-1] &= 126;
					grid[(i + (1 - hexOffset(j - 1)) < iWidth)?i + (1 - hexOffset(j - 1)):0][(j-1 >= 0)?j-1:iHeight-1] &= 125;
					grid[(i-1 >=0)?i-1:iWidth-1][j] &= 95;
					grid[(i+1 < iWidth)?i+1:0][j] &= 123;
					grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j+1 < iHeight)?j+1:0] &= 111;
					grid[(i + (1 - hexOffset(j - 1)) < iWidth)?i + (1 - hexOffset(j - 1)):0][(j+1 < iHeight)?j+1:0] &= 119;
			 		break;
			case 1:
					if (!inBoundary(i, j-1)) {
						or(i, j-1, 1);
					}
					else {
						or(i, j+1, 16);
					}
					break;
			case 2: if (!inBoundary(i+1, j-1)) {
						or(i+1, j-1, 2);
					}
					else {
						or(i+1, j+1, 8);
					}
					break;
			case 4: if (!inBoundary(i+1, j)) {
						or(i+1, j, 4);
					}
					else {
						or(i-1, j, 32);
					}
					break;
			case 8: if (!inBoundary(i+1, j+1)) {
						or(i+1, j+1, 8);
					}
					else {
						or(i+1, j-1, 2);
					}
					break;
			case 16: if (!inBoundary(i, j+1)) {
						 or(i, j+1, 16);
					 }
					 else {
						 or(i, j-1, 1);
					 }
					 break;
			case 32: if (!inBoundary(i-1, j)) {
						 or(i-1, j, 32);
					 }
					 else {
						 or(i+1, j, 4);
					 }
					 break;
			case 36: if (rand.nextInt(2) == 0) {
						or(i, j-1, 1);
						or(i+1, j+1, 8);
					 }
					 else {
						 or(i+1, j-1, 2);
						 or(i, j+1, 16);
					 }
					 break;
			case 38: or(i, j-1, 1);
					 or(i+1, j-1, 2);
					 or(i+1, j+1, 8);
					 break;
			case 54: if (rand.nextInt(2) == 0) {
					 or(i, j-1, 1);
					 or(i+1, j, 4);
					 or(i+1, j+1, 8);
					 or(i-1, j, 32);
					 }
					 else {
						 or(i, j-1, 1);
						 or(i+1, j-1, 2);
						 or(i, j+1, 16);
						 or(i-1, j, 32);
					 }
					 break;
			case 42: grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j-1 >= 0)?j-1:iHeight-1] |= 1;
			 		 grid[(i+1 < iWidth)?i+1:0][j] |= 4;
			 		 grid[(i - hexOffset(j + 1) >=0)?i - 1 + (1 - hexOffset(j + 1)):iWidth-1][(j+1 < iHeight)?j+1:0] |= 16;
			 		 break;
			case 31: grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j-1 >= 0)?j-1:iHeight-1] |= 1; // 1 = NW
					 grid[(i + (1 - hexOffset(j - 1)) < iWidth)?i + (1 - hexOffset(j - 1)):0][(j-1 >= 0)?j-1:iHeight-1] |= 2; // 2 = NE
					 grid[(i - hexOffset(j + 1) >=0)?i - 1 + (1 - hexOffset(j + 1)):iWidth-1][(j+1 < iHeight)?j+1:0] |= 8; // 8 = SW
					 grid[(i + (1 - hexOffset(j + 1)) < iWidth)?i + (1 - hexOffset(j + 1)):0][(j+1 < iHeight)?j+1:0] |= 16; // 16 = SE
					 grid[(i+1 < iWidth)?i+1:0][j] |= 8; // 8 = E
					 break;
			// Collisions with a solid body
/*			case 64: grid[i][j] |= 64;
					 break; */
//			case 68: grid[i-1][j] |= 32;
//					 grid[i][j] = grid[i][j] - 4;
/*					 if ((oldGrid[i-1][j] & 4) == 0) {
						 grid[i][j] &= 123;
					 }
					 break; */
			default: grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j-1 >= 0)?j-1:iHeight-1] |= (controlVector & 1); // 1 = NW
					 grid[(i + (1 - hexOffset(j - 1)) < iWidth)?i + (1 - hexOffset(j - 1)):0][(j-1 >= 0)?j-1:iHeight-1] |= (controlVector & 2); // 2 = NE
					 grid[(i - hexOffset(j - 1) >=0)?i - 1 + (1 - hexOffset(j - 1)):iWidth-1][(j+1 < iHeight)?j+1:0] |= (controlVector & 16); // 16 = SW
					 grid[(i + (1 - hexOffset(j + 1)) < iWidth)?i + (1 - hexOffset(j + 1)):0][(j+1 < iHeight)?j+1:0] |= (controlVector & 8); // 8 = SE
					 grid[(i+1 < iWidth)?i+1:0][j] |= (controlVector & 4); // 4 = E
					 grid[(i-1 >=0)?i-1:iWidth-1][j] |= (controlVector & 32); // 32 = W
					 grid[i][j] |= (controlVector & 64);
					 break;
			}
	}

	
	private double averageVelocity2(int i, int j, int size) {
		double[] cumulation = new double[iMode];
		double total = 0.0;
		double sum = 0.0;
		
		for(int x = i; x < i + size; x++) {
			for(int y = j; y < j + size; y++) {
				for(int q = 0; q < iMode; q++) {
					if ((grid[x][y] & (1 << q)) == (1 << q)) {
						cumulation[q] = cumulation[q] + 1;
					}
				}
			}
		}
		
		// Replaces single line below
		for(int w = 0; w < iMode; w++) {
			total = total + cumulation[w];
		}
		
		for(int q = 0; q < iMode; q++) {
			cumulation[q] = cumulation[q] * (q + 1);
		}
		
		// Replaces single line below
		for(int w = 0; w < iMode; w++) {
			sum = sum + cumulation[w];
		}		
		return sum/total;
	}
	
	private double meanOccupationNumber(int i, int j, int direction) {
		double total = 0.0;
		int cellCount = 0;
		
		for(int x = i; x < i + MACRO_CELL_SIZE; x++) {
			for(int y = j; y < j + MACRO_CELL_SIZE; y++) {
				if ((grid[x][y] & direction) == direction) {
					total = total + 1;
				}
				else {
					total = total + 0;
				}
				cellCount++;
			}
		}
		return total / cellCount;
	}
	
	private double density(int i, int j) {
		double total = 0.0;
		
		for(int x = 0; x < iMode; x++) {
			total = total + meanOccupationNumber(i, j, 1 << x);
		}
		return total;
	}
	
	private Vector momentumDensity(int i, int j) {
		Vector result = new Vector();
		
		for(int x = 0; x < iMode; x++) {
			result.setX(result.getX() + (c[x].getX() * meanOccupationNumber(i, j, 1 << x)));
			result.setY(result.getY() + (c[x].getY() * meanOccupationNumber(i, j, 1 << x)));
		}
		System.out.println("momentumDensity(): (" + result.getX() + ", " + result.getY() + ")");
		return result;
	}
	
	private double log_2(double x) {
		return (Math.log(x)/Math.log(2));
	}
	
	public void displayCollisionCounts() {
		for(int i = 0; i < 64; i++) {
			System.out.println("Collision type: " + i + ", count: " + collisionCount[i]);
		}
	}
	
	private void or(int x, int y, int value) {
		int realX = 0;
		int realY = 0;
		
		if (iMode == 4) {
			realX = x;
			realY = y;
			if (realX < 0) {
				realX = iWidth - 1;
			}
			else if (realX >= iWidth) {
				realX = realX - iWidth;
			}
			
			if (y < 0) {
				realY = iHeight - 1;
			}
			else if (y >= iHeight) {
				realY = realY - iHeight;
			}
			grid[realX][realY] = grid[realX][realY] | value;
		}
		else if (iMode == 6) {
			if (value == 4 || value == 32) {
				realX = x;
			}
			else {
				realX = x - hexOffset(y);				
			}
			realY = y;
			
			if (realX < 0) {
				System.out.println("realX = " + realX);
				realX = iWidth - 1;
			}
			else if (realX >= iWidth) {
				realX = realX - iWidth;
			}
			
			if (y < 0) {
				realY = iHeight - 1;
			}
			else if (y >= iHeight) {
				realY = realY - iHeight;
			}
			if (!inBoundary(realX, realY)) {
				grid[realX][realY] = grid[realX][realY] | value;
			}
		}
	}
	
	private int get(int x, int y, boolean lineChanged) {
		int realX = 0;
		int realY = 0;
		
		if (iMode == 4) {
			realX = x;
			realY = y;
			if (realX < 0) {
				realX = iWidth - 1;
			}
			else if (realX >= iWidth) {
				realX = realX - iWidth;
			}
			
			if (y < 0) {
				realY = iHeight - 1;
			}
			else if (y >= iHeight) {
				realY = realY - iHeight;
			}
			return grid[realX][realY];
		}
		else if (iMode == 6) {
			if (lineChanged) {
				realX = x - hexOffset(y);
			}
			else {
				realX = x;
			}
			realY = y;
			if (realX < 0) {
				realX = iWidth - 1;
			}
			else if (realX >= iWidth) {
				realX = 0;
			}
			
			if (y < 0) {
				realY = iHeight - 1;
			}
			else if (y >= iHeight) {
				realY = 0;
			}
			return grid[realX][realY];
		}
		else {
			return 0;
		}
	}
	
	private int cellCount(int gridPoint) {
		int retVal = 0;
		
		for(int q = 0; q < 4; q++) {
			if ((gridPoint & (1 << q)) == (1 << q)) {
				retVal = retVal + 1;
			}
		}
		return retVal;
	}
	
	private int midVal(int gridPoint) {
		int retVal = 0;
		
		for(int q = 0; q < 4; q++) {
			if ((gridPoint & (1 << q)) == (1 << q) && retVal == 0) {
				retVal = 1 << (q + 1);
			}
		}
		return retVal;
	}
	
	public void render(Graphics g) {
		int offset;
		double av = 0.0;
		double endPointI = 0.0;
		double endPointJ = 0.0;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, iWidth*CELL_WIDTH, iHeight*CELL_HEIGHT*2);
		g.setColor(Color.BLACK);
		
		for(int i = 0; i < iWidth; i++) {
			for(int j = 0; j < iHeight; j++) {
				if ((j == 0 || j % 2 == 0) && iMode == 6) {
					offset = 5;
				}
				else {
					offset = 0;
				}
				
				if (iMode == 4) {
					if (i % MACRO_CELL_SIZE == 0 && j % MACRO_CELL_SIZE == 0 && i < iWidth - MACRO_CELL_SIZE && j < iHeight - MACRO_CELL_SIZE) {
						av = averageVelocity2(i, j, MACRO_CELL_SIZE); // % 4
						System.out.println("Average velocity: " + av);
						endPointI = i*CELL_WIDTH-(int)((MACRO_CELL_SIZE-1)*CELL_WIDTH * Math.cos(av * step));
						endPointJ = j*CELL_HEIGHT+(int)((MACRO_CELL_SIZE-1)*CELL_HEIGHT * Math.sin(av * step));
						g.drawLine(i*CELL_WIDTH, j*CELL_HEIGHT, (int)endPointI, (int)endPointJ);
						
						if (av > 0) {
							g.drawLine((int)endPointI, (int)endPointJ, (int)(endPointI - 6*Math.sin((av*step) - (Math.PI/4))), (int)(endPointJ - 6*Math.cos((av*step) - (Math.PI/4))));
						}
					}
					switch(grid[i][j]) {
					case 0: break;
					case 1: g.drawString("|", i * 10 + offset, (j + iHeight) * 10);
							break;
					case 2: g.drawString("-", i * 10 + offset, (j + iHeight) * 10);
							break;
					case 4: g.drawString("|", i * 10 + offset, (j + iHeight) * 10);
							break;
					case 8: g.drawString("-", i * 10 + offset, (j + iHeight) * 10);
							break;
					default: g.drawString("*", i * 10 + offset, (j + iHeight) * 10);
							 break;
						}
				}
				else if (iMode == 6) {
					if (i % MACRO_CELL_SIZE == 0 && j % MACRO_CELL_SIZE == 0 && i < iWidth - MACRO_CELL_SIZE && j < iHeight - MACRO_CELL_SIZE && (grid[i][j] < 64)) {
						Vector mdArrow = momentumDensity(i, j);
						
						endPointI = (i + mdArrow.getX()) * CELL_WIDTH * MAGNIFICATION_FACTOR; // CELL_WIDTH
						endPointJ = (j + mdArrow.getY()) * CELL_HEIGHT * MAGNIFICATION_FACTOR;
						g.drawLine(i*CELL_WIDTH, j*CELL_HEIGHT, (int)endPointI, (int)endPointJ); // CELL_WIDTH						
					}
					
					switch(grid[i][j]) {
					case 0: break;
					case 1: g.drawString("\\", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							break;
					case 2: g.drawString("/", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							break;
					case 4: g.drawString("-", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							break;
					case 8: g.drawString("\\", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							break;
					case 16: g.drawString("/",  i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							 break;
					case 32: g.drawString("-",  i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							 break;
					case 64: g.drawString("@",  i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
					 		 break;
					default:
							 g.drawString("*", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
							 break;
					}
					
					if (inBoundary(i, j)) {
						g.drawString("@", i * CELL_WIDTH + offset, (j + iHeight) * CELL_HEIGHT);
					}
				}
			}
		}
	}
	
	public boolean inBoundary(int i, int j) {
		for(BoundaryPoint p : boundaryPoints) {
			if (p.getX() == i && p.getY() == j) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HexagonalGrid hg = new HexagonalGrid(35, 100, 6);
	}

}
