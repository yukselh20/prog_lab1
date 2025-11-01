package classes;

import java.util.ArrayList;
import java.util.List;

import exeptions.HumorOverdoseException;
import interfaces.Printable;

// Class ReportNewspapers
public class ReportNewspapers extends Newspapers implements Printable {
    private List<String> reports;

    public ReportNewspapers(String name) {
        super(name);
        this.reports = new ArrayList<>();
    }
    
    public void addReport(String report) {
        reports.add(report);
        System.out.println(getName() + " added a report: " + report);
    }

    @Override
    public void printReport(String report) {
        System.out.println(getName() + " is publishing reports...");
    }

	@Override
	public void printJoke(String joke) throws HumorOverdoseException {
        throw new UnsupportedOperationException("Jokes are not supported in Report Newspapers.");
		
	}
	@Override
	public String toString() {
        return "ReportNewspapers{" + super.toString() + ", reports=" + reports + "}";
    }
}
