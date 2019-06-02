package Essintials;
import java.util.ArrayList;


public class Chromosome implements Comparable <Chromosome>{
	private ArrayList<Integer> vector;
	private int legal;
	private int illegal;
	//CONSTRUCTORS
	public Chromosome() {
		setVector(new ArrayList<Integer>());
		setLegal(0);
		setIllegal(0);
	}
	public Chromosome(Chromosome temp){
		this(temp.getVector(),temp.getLegal(),temp.getIllegal());
	}
	public Chromosome(ArrayList<Integer> vector,int legal,int illegal) {
		setVector(vector);
		setLegal(legal);
		setIllegal(illegal);
	}
	//FUNCTIONS
	public void addValue(int val) {
		getVector().add(val);
	}
	public int getValueAt(int index) {
		return getVector().get(index);
	}
	public void addPenelty() {
		setIllegal(getIllegal()+1);
	}
	public void addPass() {
		setLegal(getLegal()+1);
	}
	//COMPARE FUNCTION
	@Override
	public int compareTo(Chromosome crm) {
		return ((this.legal-this.illegal)-(crm.legal-crm.illegal));
	}
	// GETTERS & SETTERS
	public ArrayList<Integer> getVector() {
		return vector;
	}
	public void setVector(ArrayList<Integer> vector) {
		this.vector = vector;
	}
	public int getIllegal() {
		return illegal;
	}
	public void setIllegal(int illegal) {
		this.illegal = illegal;
	}
	public int getLegal() {
		return legal;
	}
	public void setLegal(int legal) {
		this.legal = legal;
	}
}
