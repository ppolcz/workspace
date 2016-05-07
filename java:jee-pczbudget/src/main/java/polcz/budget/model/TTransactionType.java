package polcz.budget.model;

public enum TTransactionType {
	simple,
	transfer,
	pivot,
	exchange;
	
	public String getName() {
		return this.name();
	}
}
