package viewer;

/**
 * The <code>ComparableArray</code> is actually not an array... or is it? It's
 * an object meant to be used in an array to make it easy to sort the columns
 * of a 2D array. The <code>ComparableArray</code> represents one row of a
 * table, essentially. By implementing the <code>Comparable</code> interface,
 * it becomes possible to sort an array of <code>ComparableArray</code>s by the
 * values of a particular column.
 * 
 * @author Jordan
 *
 */
public class ComparableArray implements Comparable<ComparableArray> {
	private int valToCompare;
	private String name;
	private int wins;
	private int battles;
	private double winrate;
	
	/**
	 * Constructor for the <code>ComparableArray</code> class.
	 * 
	 * @param name			The character name being represented in the row of
	 * 						the table. This is the first column (index 0).
	 * @param wins			The number of wins for the character. Stored as a
	 * 						<code>double</code>, but should always be a whole
	 * 						number. This is the second column (index 1).
	 * @param battles		The number of battles this character has participated
	 * 						in. Once again, stored as a <code>double</code> but
	 * 						should always be a whole number. This is the third
	 * 						column (index 2).
	 * @param valToCompare	The column that will be used for comparisons. For
	 * 						each <code>ComparableArray</code> table, the column
	 * 						used for sorting is specified at time of creation.
	 * 						Only the value 3 is actually checked for. If you
	 * 						specify 3, then the winrate (<code>wins</code>/<code>battles</code>)
	 * 						is used for sorting. Any other value, and <code>battles</code>
	 * 						is used as the sorting value.
	 */
	public ComparableArray(String name, double wins, double battles, int valToCompare) {
		this.name = name;
		this.wins = (int)wins;
		this.battles = (int)battles;
		this.winrate = wins / battles;
		this.valToCompare = valToCompare;
	}
	
	/**
	 * @return	The entry in the first column, the name of the character
	 * 			represented in this row.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return	The entry in the second column, the number of wins this character
	 * 			has.
	 */
	public int getWins() {
		return wins;
	}
	
	/**
	 * @return	The entry in the third column, the number of battles this character
	 * 			has participated in.
	 */
	public int getBattles() {
		return battles;
	}
	
	/**
	 * @return	The winrate for this character, or <code>wins</code>/<code>battles</code>.
	 */
	public double getWinrate() {
		return winrate;
	}

	/**
	 * Compares this <code>ComparableArray</code> with the specified
	 * <code>ComparableArray</code>. Which specific values are compared depends
	 * on the <code>valToCompare</code> field.
	 * <br><br>
	 * If <code>valToCompare</code> is 3, then the <code>winrate</code> will be
	 * compared. If both <code>ComparableArray</code>s have a <code>winrate</code>
	 * of <code>NaN</code> -- meaning that neither have fought in any battle,
	 * then they are equal. Otherwise, a <code>winrate</code> of <code>NaN</code>
	 * automatically means that the particular <code>ComparableArray</code> is
	 * less than the other one. This ensures that values of <code>NaN</code> are
	 * always at the bottom of the list.
	 * <br><br>
	 * If neither <code>ComparableArray</code> has a <code>winrate</code> of
	 * <code>NaN</code>, then their winrates will be directly compared using
	 * the <code>Double</code> class' <code>compare()</code> method. If the
	 * <code>winrate</code>s are equal, then the <code>battle</code> field will
	 * be compared using the <code>Integer compare()</code> method. If that
	 * method returns 0, then finally the result of comparing the <code>name</code>
	 * field will be inverted and returned. Inverting ensures that names are in
	 * alphabetical order.
	 * <br><br>
	 * If any value other than 3 is specified for <code>valToCompare</code>,
	 * then the <code>battles</code> field is compared using the <code>Integer
	 * compare()</code> method.
	 */
	public int compareTo(ComparableArray o) {
		if(valToCompare == 3) {
			if(battles == 0 && o.getBattles() == 0) {
				return 0;
			}
			else if(battles == 0 && !(o.getBattles() == 0)) {
				return -1;
			}
			else if(battles != 0 && o.getBattles() == 0) {
				return 1;
			}
			
			int doubComp = Double.compare(winrate, o.getWinrate());
			if(doubComp == 0) {
				int batComp = Integer.compare(battles, o.getBattles());
				if(batComp == 0) {
					return -(name.compareTo(o.getName()));
				}
				return batComp;
			}
			return doubComp;
		}
		else {
			return Integer.compare(battles, o.getBattles());
		}
	}
	
	public String toString() {
		return name + " - " + StatsWindow.printDouble(winrate * 100) + "% (" + wins + "/" + battles + ")";
	}

}