package dataStructures;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OctreePRCube<E>
{
	private final int cellCapacity;
	private final int maxDepth;
	private final long min, max;
	
	private OctreeCell root;
	
	public OctreePRCube(int cellCapacity, int maxDepth, long range)
	{
		this.cellCapacity = cellCapacity;
		this.maxDepth = maxDepth;
		this.min = -range;
		this.max = range;	
		this.root  = new OctreeCell(1, 0, 0, 0, range);
	}
	
	public OctreePRCube(int cellCapacity, int maxDepth, long min, long max)
	{
		this.cellCapacity= cellCapacity;
		this.maxDepth = maxDepth;
		this.min = min;
		this.max = max;
		this.root  = new OctreeCell(1, (max-min)/2, (max-min)/2, (max-min)/2, max-min);
	}

	public class OctreeCell
	{
		private final long centerX, centerY, centerZ, range;
		private final int depth;
		private List<OctreeElement> elements = new ArrayList<>(cellCapacity);
		private OctreeCell[] children;
		
		public OctreeCell(int depth, long centerX, long centerY, long centerZ, long range)
		{
			this.depth = depth;
			this.centerX = centerX;
			this.centerY = centerY;
			this.centerZ = centerZ;
			this.range = range;
		}
		
		public long[] getCenterPos()
		{
			long[] pos = {centerX, centerY, centerZ}; 
			return pos;
		}
		
		public long getRange()
		{
			return range;
		}
		
		public long[] getCornerPos(int i)
		{
			long[] pos = new long[3];
			switch(i)
			{
				case 0:
					pos[0] = centerX+range;
					pos[1] = centerY+range;
					pos[2] = centerZ+range;
					break;
				case 1:
					pos[0] = centerX+range;
					pos[1] = centerY+range;
					pos[2] = centerZ-range;
					break;
				case 2:
					pos[0] = centerX+range;
					pos[1] = centerY-range;
					pos[2] = centerZ+range;
					break;
				case 3:
					pos[0] = centerX+range;
					pos[1] = centerY-range;
					pos[2] = centerZ-range;
					break;
				case 4:
					pos[0] = centerX-range;
					pos[1] = centerY+range;
					pos[2] = centerZ+range;
					break;
				case 5:
					pos[0] = centerX-range;
					pos[1] = centerY+range;
					pos[2] = centerZ-range;
					break;
				case 6:
					pos[0] = centerX-range;
					pos[1] = centerY-range;
					pos[2] = centerZ+range;
					break;
				case 7:
					pos[0] = centerX-range;
					pos[1] = centerY-range;
					pos[2] = centerZ-range;
					break;
			}
			
			return pos;
		}
	}
	
	private class OctreeElement
	{
		private final E value;
		private final long x, y, z;
		
		public OctreeElement(E value, long x, long y, long z)
		{
			this.value = value;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public String toString()
		{
			return "x="+x+" y="+y+" "+z+" "+value.toString();
		}
	}

	public boolean add(E valueToAdd, long x, long y, long z)
	{
		if(x < min || x > max || y < min || y > max || z < min || z > max) return false;
		
		OctreeCell currentCell = getCell(x, y, z);
		
		if(currentCell.elements.size() == cellCapacity && currentCell.depth != maxDepth)
		{
			divideCell(currentCell);

			if(x >= currentCell.centerX)
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[0] : currentCell.children[1]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[2] : currentCell.children[3]);	
				}				
			}
			else
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[4] : currentCell.children[5]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[6] : currentCell.children[7]);	
				}				
			}
		}

		addRecursive(currentCell, currentCell.depth, valueToAdd, x, y, z);		

		return true;
	}

	private void addRecursive(OctreeCell cell, int depth, E valueToAdd, long x, long y, long z)
	{
		OctreeCell currentCell = cell;
		
		if(currentCell.elements.size() == cellCapacity && depth != maxDepth)
		{
			divideCell(currentCell);

			if(x >= currentCell.centerX)
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[0] : currentCell.children[1]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[2] : currentCell.children[3]);	
				}				
			}
			else
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[4] : currentCell.children[5]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[6] : currentCell.children[7]);	
				}				
			}

			addRecursive(currentCell, depth+1, valueToAdd, x, y, z);
		}		
		else currentCell.elements.add(new OctreeElement(valueToAdd, x, y, z));
	}
	
	private void divideCell(OctreeCell cellToDivide)
	{
		long halfRange = cellToDivide.range/2;
		long leftCenterX = cellToDivide.centerX-halfRange;
		long rightCenterX = cellToDivide.centerX+halfRange;
		long upperCenterY = cellToDivide.centerY+halfRange;
		long bottomCenterY = cellToDivide.centerY-halfRange;
		long frontCenterZ = cellToDivide.centerZ+halfRange;
		long rearCenterZ = cellToDivide.centerZ-halfRange;	
		int childsDepth = cellToDivide.depth + 1;
		
		cellToDivide.children = (OctreePRCube<E>.OctreeCell[]) Array.newInstance(OctreeCell.class, 8);
		cellToDivide.children[0] = new OctreeCell(childsDepth, rightCenterX, upperCenterY, frontCenterZ, halfRange);
		cellToDivide.children[1] = new OctreeCell(childsDepth, rightCenterX, upperCenterY, rearCenterZ, halfRange);
		cellToDivide.children[2] = new OctreeCell(childsDepth, rightCenterX, bottomCenterY, frontCenterZ, halfRange);
		cellToDivide.children[3] = new OctreeCell(childsDepth, rightCenterX, bottomCenterY, rearCenterZ, halfRange);
		
		cellToDivide.children[4] = new OctreeCell(childsDepth, leftCenterX, upperCenterY, frontCenterZ, halfRange);
		cellToDivide.children[5] = new OctreeCell(childsDepth, leftCenterX, upperCenterY, rearCenterZ, halfRange);
		cellToDivide.children[6] = new OctreeCell(childsDepth, leftCenterX, bottomCenterY, frontCenterZ, halfRange);
		cellToDivide.children[7] = new OctreeCell(childsDepth, leftCenterX, bottomCenterY, rearCenterZ, halfRange);
		
		for(OctreeElement element : cellToDivide.elements)
		{
			if(element.x >= cellToDivide.centerX)
			{
				if(element.y >= cellToDivide.centerY)
				{
					cellToDivide.children[element.z >= cellToDivide.centerZ ? 0 : 1].elements.add(element);
				}
				else
				{
					cellToDivide.children[element.z >= cellToDivide.centerZ ? 2 : 3].elements.add(element);
				}				
			}
			else
			{
				if(element.y >= cellToDivide.centerY)
				{
					cellToDivide.children[element.z >= cellToDivide.centerZ ? 4 : 5].elements.add(element);	
				}
				else
				{
					cellToDivide.children[element.z >= cellToDivide.centerZ ? 6 : 7].elements.add(element);
				}				
			}
		}
		
		cellToDivide.elements = null;
	}
	
	private String getStringRepresentation(OctreeCell cell, int depth)
	{
		if(isEmptyLeaf(cell)) return "";
		
		StringBuffer buffer = new StringBuffer();

		if(cell.children == null)
		{
			buffer.append("depth = " + depth + "\n");
			buffer.append("size = " + cell.elements.size() + "\n");	
			buffer.append("center = ("+cell.centerX+" "+cell.centerY+" "+cell.centerZ+")\n");
			buffer.append("range = ("+cell.range+" "+cell.range+" "+cell.range+")\n");	
		}
		else
		{
			for(OctreeCell c : cell.children)
			{
				buffer.append(getStringRepresentation(c, depth+1));
			}
		}
		
		return buffer.toString();
	}
	
	private boolean isEmptyLeaf(OctreeCell cell)
	{
		return (cell.children == null && cell.elements.isEmpty());
	}
	
	public boolean contains(E element)
	{
		return contains(root, element);	
	}
	
	private boolean contains(OctreeCell cell, E element)
	{
		if(cell.children == null)
		{
			for(OctreeElement e : cell.elements)
			{
				if(element.equals(e.value)) return true;
			}
			
			return false;
		}
		
		for(OctreeCell child : cell.children)
		{
			if(contains(child, element)) return true;
		}
		return false;
	}
	
	public boolean contains(E element, long x, long y, long z)
	{
		OctreeCell cell = getCell(x, y, z);
		
		for(OctreeElement e : cell.elements)
		{
			if(element.equals(e.value)) return true;
		}
		
		return false;
	}
	
	private OctreeCell getCell(long x, long y, long z)
	{
		OctreeCell currentCell = root;
		
		while(true)
		{
			if(currentCell.children == null) break;
			
			if(x >= currentCell.centerX)
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[0] : currentCell.children[1]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[2] : currentCell.children[3]);	
				}				
			}
			else
			{
				if(y >= currentCell.centerY)
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[4] : currentCell.children[5]);	
				}
				else
				{
					currentCell = (z >= currentCell.centerZ ? currentCell.children[6] : currentCell.children[7]);	
				}				
			}
		}
		
		return currentCell;
	}
	
	public boolean remove(E element)
	{	
		return remove(element, root);
	}
	
	public boolean remove(E element, OctreeCell cell)
	{
		if(cell.children == null)
		{
			Iterator<OctreeElement> iterator = cell.elements.iterator();
			
			while(iterator.hasNext())
			{
				if(iterator.next().value.equals(element))
				{
					iterator.remove();
					return true;
				}
			}
		}
		else
		{
			for(OctreeCell child : cell.children)
			{
				if(remove(element, child)) return true;
			}
		}
		
		return false;
	}
	
	public boolean remove(E element, long x, long y, long z)
	{
		return remove(root, element, x, y, z);
	}

	private boolean remove(OctreeCell cell, E element, long x, long y, long z)
	{
		if(cell.elements != null) return removeElementFromCell(cell, element);

		OctreeCell cellInPos = getCell(x, y, z);

		if(remove(cellInPos, element, x, y, z))
		{ 
			mergeCellsIfNeeded(cell);
			
			return true;
		}
		
		return false;
	}

	private void mergeCellsIfNeeded(OctreeCell cell)
	{
		for(OctreeCell c : cell.children)
		{
			if(c.elements == null) return;
		}

		int size = 0;
		for(OctreeCell c : cell.children)
		{
			size += c.elements.size();
		}

		if(size != cellCapacity) return;
		
		cell.elements = new ArrayList<>(cellCapacity);
		for(OctreeCell c : cell.children)
		{
			cell.elements.addAll(c.elements);
		}

		cell.children = null;
	}

	public List<E> getElements(Criterion criterion)
	{
		return getElements(criterion, root);
	}
	
	private List<E> getElements(Criterion criterion, OctreeCell cell)
	{
		List<E> elements = new ArrayList<E>();

		switch(criterion.belongs(cell))
		{
			case OUTSIDE: return Collections.emptyList();		
			case INSIDE:				
				if(cell.children == null)
				{
					for(OctreeElement e : cell.elements)
					{
						elements.add(e.value);
					}
				}
				else
				{
					for(OctreeCell c : cell.children)
					{
						elements.addAll(getElements(c));
					}
				}
				return elements;
				
			case PARTIAL: 
				if(cell.children == null)
				{
					/** TODO, add second criterion */
					for(OctreeElement e : cell.elements)
					{
						elements.add(e.value);
					}
				}
				else
				{
					for(OctreeCell c : cell.children)
					{
						elements.addAll(getElements(criterion, c));
					}
				}
				return elements;
				
			default: return Collections.emptyList();
		}	
	}
	
	private List<E> getElements(OctreeCell cell)
	{
		List<E> elements = new ArrayList<E>();
		
		if(cell.children == null)
		{
			for(OctreeElement e : cell.elements)
			{
				elements.add(e.value);
			}	
		}
		else
		{
			for(OctreeCell c : cell.children)
			{
				elements.addAll(getElements(c));
			}
		}

		return elements;
	}
	
	public List<E> getElementsNew(Criterion criterion)
	{
		List<OctreeCell> cells = new LinkedList<OctreeCell>();
		
		int elementsCount = getElementsNew(criterion, root, cells);
		
		List<E> elements = new ArrayList<>(elementsCount);
		
		for(OctreeCell cell : cells)
		{
			for(OctreeElement oe : cell.elements)
			{
				elements.add(oe.value);
			}		
		}
		
		return elements;
	}
	
	private int getElementsNew(Criterion criterion, OctreeCell cell, List<OctreeCell> cells)
	{
		switch(criterion.belongs(cell))
		{		
			case OUTSIDE: return 0;		
			case INSIDE: return getElementsNew(cell, cells);				
			case PARTIAL: 
				if(cell.children == null)
				{
					/** TODO, add second criterion */
					cells.add(cell);
					return cell.elements.size();
				}
				else
				{
					int count = 0;
					for(OctreeCell c : cell.children)
					{
						count += getElementsNew(criterion, c, cells);
					}
					return count;
				}				
			default: return 0;
		}	
	}
	
	private int getElementsNew(OctreeCell cell, List<OctreeCell> cells)
	{
		if(cell.children == null)
		{
			cells.add(cell);
			return cell.elements.size();
		}
		else
		{
			int count = 0;
			for(OctreeCell c : cell.children)
			{
				count += getElementsNew(c, cells);
			}
			return count;
		}	
	}
	
	public enum Covering
	{
		PARTIAL, INSIDE, OUTSIDE
	}
	
	public interface Criterion
	{
		public abstract Covering belongs(OctreePRCube.OctreeCell cell);
	}
	
	private boolean removeElementFromCell(OctreeCell cell, E element)
	{
		Iterator<OctreeElement> iterator = cell.elements.iterator();
			
		while(iterator.hasNext())
		{
			if(iterator.next().value.equals(element))
			{
				iterator.remove();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("QuadTree String representation:\n");
		buffer.append("Cell capacity = " + cellCapacity + "\n");
		buffer.append("Min x = " + min + "\n");
		buffer.append("Max x = " + max + "\n");
		buffer.append("Min y = " + min + "\n");
		buffer.append("Max y = " + max + "\n");
		buffer.append("Min z = " + min + "\n");
		buffer.append("Max z = " + max + "\n\n");
		buffer.append(getStringRepresentation(root, 1));
		
		return buffer.toString();
	}
}
