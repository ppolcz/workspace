package polcz.budget.model;

public enum UgyletType {
	simple,
	transfer,
	pivot,
	exchange;
	
	public String getName() {
		return this.name();
	}
}
