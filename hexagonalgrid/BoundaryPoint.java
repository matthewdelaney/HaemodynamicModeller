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
 * Filename: BoundaryPoint.java
 * Purpose : Representation of an arterial boundary-point
 * Date    : 8/3/2018
 * @author Matthew Delaney
 */
package hexagonalgrid;

public class BoundaryPoint {
	private int x;
	private int y;
	
	public BoundaryPoint() {
		x = 0;
		y = 0;
	}
	
	public BoundaryPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
