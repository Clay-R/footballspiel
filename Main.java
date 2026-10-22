import java.util.*;

enum Position {
    QB, RB, WR, TE, OT, OG, C, DE, DT, LB, CB, S, K, P
}

class Player {
    private String name;
    private Position position;
    private int age;
    private int overallRating;
    private double salaryInMillions;
    private int contractYearsRemaining;
    private Map<String, Integer> attributes;

    public Player(String name, Position position, int age, double salaryInMillions, int contractYearsRemaining) {
        this.name = name;
        this.position = position;
        this.age = age;
        this.salaryInMillions = salaryInMillions;
        this.contractYearsRemaining = contractYearsRemaining;
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String attr, int value) {
        attributes.put(attr, value);
        calculateOverall();
    }

    public int getAttribute(String attr) {
        return attributes.getOrDefault(attr, 50);
    }

    public void calculateOverall() {
        switch (position) {
            case QB:
                this.overallRating = (int) (getAttribute("throwPower") * 0.4 + getAttribute("accuracy") * 0.4 + getAttribute("awareness") * 0.2);
                break;
            case RB:
                this.overallRating = (int) (getAttribute("speed") * 0.4 + getAttribute("elusiveness") * 0.3 + getAttribute("carrying") * 0.3);
                break;
            case WR:
            case TE:
                this.overallRating = (int) (getAttribute("speed") * 0.3 + getAttribute("catching") * 0.4 + getAttribute("routeRunning") * 0.3);
                break;
            default:
                this.overallRating = (int) attributes.values().stream().mapToInt(Integer::intValue).average().orElse(70);
                break;
        }
    }

    public String getName() { return name; }
    public Position getPosition() { return position; }
    public int getAge() { return age; }
    public int getOverallRating() { return overallRating; }
    public double getSalaryInMillions() { return salaryInMillions; }
    public int getContractYearsRemaining() { return contractYearsRemaining; }

    @Override
    public String toString() {
        return String.format("%s (%s) | OVR: %d | Age: %d | Salary: $%.1fM | Yrs: %d", 
                name, position, overallRating, age, salaryInMillions, contractYearsRemaining);
    }
}

class Team {
    private String name;
    private String division;
    private List<Player> roster;
    private double salaryCap = 255.4;
    private int wins = 0;
    private int losses = 0;

    public Team(String name, String division) {
        this.name = name;
        this.division = division;
        this.roster = new ArrayList<>();
    }

    public double getTotalPayroll() {
        double total = 0;
        for (Player p : roster) {
            total += p.getSalaryInMillions();
        }
        return total;
    }

    public double getCapSpace() {
        return salaryCap - getTotalPayroll();
    }

    public boolean addPlayer(Player player) {
        if (getCapSpace() >= player.getSalaryInMillions()) {
            roster.add(player);
            return true;
        }
        return false;
    }

    public void removePlayer(Player player) {
        roster.remove(player);
    }

    public String getName() { return name; }
    public String getDivision() { return division; }
    public List<Player> getRoster() { return roster; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public void addWin() { wins++; }
    public void addLoss() { losses++; }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("NFL Manager Java Console Application Ready.");
    }
}
