//package
package program;

//java imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.util.Vector;

//class
public final class Map {
	
	//attributes
	private int width = 100;
	private int height = 100;
	private Vector<Grid> grids = new Vector<Grid>();
	private ValueGenerator valueGenerator = new ValueGenerator();
	
	//constructor
	public Map() {
		initialize();
		buildup();
	}
	
	//constructor
	public Map(int width, int height) throws Exception {
		if (width < 1) {
			throw new Exception("Width is not positive.");
		}
		if (height < 1) {
			throw new Exception("Height is not positive.");
		}
		this.width  = width;
		this.height = height;
		initialize();
		buildup();
	}
	
	//method
	public int getBaseValue() {
		return valueGenerator.getBaseValue();
	}
	
	//method
	@SuppressWarnings("serial")
	public JPanel getJPanel() {
		return new JPanel() {			
			
			//overwritten method
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);                
                Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.setColor(Color.GREEN);
                for (Grid grid: grids) {
                	switch (grid.getKind()) {
                		case OCEAN:
                			graphics2D.setColor(new Color(program.Color.BLUE));
                			break;
                		case DEEP_OCEAN:
                			graphics2D.setColor(Color.BLACK);
                			break;
                		case COAST:
                			graphics2D.setColor(new Color(program.Color.YELLOW));
                			break;
                		case DESERT:
                			graphics2D.setColor(new Color(program.Color.LIGHT_ORANGE));
                			break;
                		case LAND:
                			graphics2D.setColor(Color.GREEN);
                			break;
                		case HILL:
                			graphics2D.setColor(new Color(program.Color.BROWN));
                			break;
                		case MOUNTAIN:
                			graphics2D.setColor(Color.GRAY);
                			break;
                		case SNOW_MOUNTAIN:
                			graphics2D.setColor(new Color(program.Color.WHITE));
                			break;               			
                	}
                	graphics2D.drawRect(grid.getX() - 1, height - grid.getY() + 1, 1, 1);
                }
            }
            
            //overwritten method
            public Dimension getPreferredSize() {
                return new Dimension(width + 10, height + 10);
            }
        };
	}
	
	//method
	public int getMiddleX() {
		return width / 2;
	}
	
	//method
	public int getMiddleY() {
		return height / 2;
	}
	
	//method
	private void initialize() {
		try {
			//create grids
			Grid lowerLeftGrid = new Grid(1, 1);
			Grid iterator = lowerLeftGrid;
			for (int i = 1; i <= height; i++) {
				try {
					iterator.createRightGrids(width);
					iterator.createTopGrid();
					iterator = iterator.getTopGrid();
				}
				catch (Exception e) {}
			}
			try {
				iterator.createRightGrids(width);
			}
			catch (Exception e) {}
			
			//store grids in vector
			iterator = lowerLeftGrid;
			do {
				Grid iterator2 = iterator;
				do {
					grids.add(iterator2);
					try {
						iterator2 = iterator2.getRightGrid();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				while (iterator2.hasRightGrid());
				try {
					iterator = iterator.getTopGrid();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (iterator.hasTopGrid());
			}
		catch (Exception e1) {}
	}
	
	//method
	private void buildup() {
		//paint land
		for (Grid grid: grids) {
			if (grid.getX() > width / 3 && grid.getX() < 2 * width / 3 && grid.getY() > height / 3 && grid.getY() < 2 * height / 3)
				if (valueGenerator.getTrueForPropability(1) && valueGenerator.getTrueForPropability(1)) {
						grid.paintIsland(valueGenerator, (int)(1.2 * width));
				}
		}
		getCenterGrid().paintIsland(valueGenerator, width / 2);
		
		//paint desert
		for (Grid grid: grids) {
			if (grid.getKind() == GridKind.LAND && valueGenerator.getTrueForPropability(1) && valueGenerator.getTrueForPropability(1) && valueGenerator.getTrueForPropability(10)) {
				grid.paintDesert(valueGenerator, 800);
			}
		}
		
		//paint mountains
		//paint desert
		for (Grid grid: grids) {
			if (grid.getX() > width / 4 && grid.getX() < 3 * width / 4 && grid.getY() > height / 4 && grid.getY() < 3 * height / 4 && valueGenerator.getTrueForPropability(1) && valueGenerator.getTrueForPropability(1) && valueGenerator.getTrueForPropability(10)) {
				grid.paintMountains(valueGenerator, 200 + valueGenerator.getNumberBetweenZeroAndValueIncludingBounds(300));
			}
		}
		
		//paint coast
		for (Grid grid: grids) {
			if (grid.getKind() == GridKind.LAND || grid.getKind() == GridKind.DESERT || grid.getKind() == GridKind.MOUNTAIN) {
				for (Grid grid2: grid.getNeighbourGrids()) {
					if (grid2.getKind() == GridKind.OCEAN) {
						grid2.paintCoast(valueGenerator, 20);
					}
				}
			}
		}
	}
	
	//method
	private Grid getCenterGrid() {
		int middleX = getMiddleX();
		int middleY = getMiddleY();
		for (Grid grid: grids) {
			if (grid.getX() == middleX && grid.getY() == middleY) {
				return grid;
			}
		}
		return null;
	}
}