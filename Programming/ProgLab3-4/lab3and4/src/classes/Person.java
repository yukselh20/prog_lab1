package classes;

import enums.PersonStatus;

class Person extends Essence {
	private PersonStatus status;
	
	public Person(String name, PersonStatus status) {
	    super(name);
	    this.status = status;
	}
	
	public PersonStatus getStatus() {
	    return status;
	}
	
	public void setStatus(PersonStatus status) {
	    this.status = status;
	}
	 
	@Override
	public String toString() {
	    return super.toString() + ", status=" + status;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (!super.equals(obj)) return false;
	    Person person = (Person) obj;
	    return status == person.status;
	}
	
	@Override
	public int hashCode() {
	    return super.hashCode() * 31 + status.hashCode();
	}
}