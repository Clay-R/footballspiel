import java.util.*;

// ==================== POSITIONS ====================
enum Position {
    QB, RB, WR, TE, OT, OG, C, DE, DT, LB, CB, S, K, P
}

// ==================== PLAYER ====================
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

// ==================== DRAFT PICK ====================
class DraftPick {
    private int round;
    private int pickNumber;
    private String originalTeam;

    public DraftPick(int round, int pickNumber, String originalTeam) {
        this.round = round;
        this.pickNumber = pickNumber;
        this.originalTeam = originalTeam;
    }

    public int getRound() { return round; }
    public int getPickNumber() { return pickNumber; }
    public String getOriginalTeam() { return originalTeam; }

    @Override
    public String toString() {
        return "Round " + round + " Pick (" + originalTeam + ")";
    }
}

// ==================== TEAM ====================
class Team {
    private String name;
    private String division;
    private List<Player> roster;
    private List<DraftPick> draftPicks;
    private double salaryCap = 255.4; // $255.4M Cap
    private int wins = 0;
    private int losses = 0;

    public Team(String name, String division) {
        this.name = name;
        this.division = division;
        this.roster = new ArrayList<>();
        this.draftPicks = new ArrayList<>();
        
        // Initialize default 7 rounds of draft picks
        for (int r = 1; r <= 7; r++) {
            draftPicks.add(new DraftPick(r, r * 32, name));
        }
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
    public List<DraftPick> getDraftPicks() { return draftPicks; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public void addWin() { wins++; }
    public void addLoss() { losses++; }
}

// ==================== GAME ENGINE ====================
class GameEngine {
    public static void playInteractiveGame(Team userTeam, Team opponentTeam, Scanner scanner) {
        System.out.println("\n==================================================");
        System.out.println("   GAME DAY: " + userTeam.getName() + " vs " + opponentTeam.getName());
        System.out.println("==================================================");
        
        int userScore = 0;
        int opponentScore = 0;

        String[] scenarios = {
            "1st Qtr (8:45) - 4th & 2 on opponent's 45-yard line.",
            "2nd Qtr (0:30) - 3rd & Goal on the 6-yard line, down by 4 points.",
            "3rd Qtr (10:15) - Defense facing 3rd & 14 near midfield.",
            "4th Qtr (2:00) - 4th & 7 trailing by 3 points with no timeouts left."
        };

        for (int i = 0; i < scenarios.length; i++) {
            System.out.println("\n[SITUATION " + (i + 1) + "]: " + scenarios[i]);
            System.out.println("Select Play Call:");
            System.out.println("1. Aggressive Pass / Heavy Blitz");
            System.out.println("2. Safe Run Play / Zone Coverage");
            System.out.println("3. Play Action / Medium Pass");
            System.out.print("Your Call (1-3): ");

            int choice = 1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                choice = 1;
            }

            double roll = Math.random();
            if (choice == 1) {
                if (roll > 0.45) {
                    System.out.println(">> BIG PLAY SUCCESS! You pick up massive yardage (+7 PTS).");
                    userScore += 7;
                } else {
                    System.out.println(">> TURNOVER / SACK! Opponent capitalized (+3 PTS).");
                    opponentScore += 3;
                }
            } else if (choice == 2) {
                System.out.println(">> SOLID EXECUTION! Drive sustained for field goal (+3 PTS).");
                userScore += 3;
            } else {
                if (roll > 0.35) {
                    System.out.println(">> PERFECT READ! Touchdown pass (+7 PTS).");
                    userScore += 7;
                } else {
                    System.out.println(">> INCOMPLETE! Turned over on downs.");
                    opponentScore += 7;
                }
            }
        }

        System.out.println("\n--------------------------------------------------");
        System.out.println("FINAL SCORE: " + userTeam.getName() + " " + userScore + " - " + opponentScore + " " + opponentTeam.getName());
        System.out.println("--------------------------------------------------");

        if (userScore >= opponentScore) {
            userTeam.addWin();
            opponentTeam.addLoss();
            System.out.println("RESULT: VICTORY!");
        } else {
            userTeam.addLoss();
            opponentTeam.addWin();
            System.out.println("RESULT: DEFEAT.");
        }
    }
}

// ==================== MAIN GAME CONTROLLER ====================
public class Main {
    private static List<Team> leagueTeams = new ArrayList<>();
    private static List<Player> freeAgentPool = new ArrayList<>();
    private static List<Player> draftClass = new ArrayList<>();
    private static Team userTeam;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeLeague();
        
        System.out.println("==========================================");
        System.out.println("      WELCOME TO NFL MANAGER SIMULATOR    ");
        System.out.println("==========================================");

        // 1. Team Selection
        System.out.println("\nSelect your team:");
        for (int i = 0; i < leagueTeams.size(); i++) {
            System.out.println((i + 1) + ". " + leagueTeams.get(i).getName() + " (" + leagueTeams.get(i).getDivision() + ")");
        }
        System.out.print("Enter team choice (1-" + leagueTeams.size() + "): ");
        int teamChoice = Integer.parseInt(scanner.nextLine()) - 1;
        userTeam = leagueTeams.get(teamChoice);

        System.out.println("\nWelcome, General Manager of the " + userTeam.getName() + "!");

        // 2. Pre-Season Draft Event
        runDraft(scanner);

        // 3. Regular Season Loop
        boolean running = true;
        int currentWeek = 1;

        while (running && currentWeek <= 5) {
            System.out.println("\n==========================================");
            System.out.println("   WEEK " + currentWeek + " - MANAGER DASHBOARD");
            System.out.println("   Team Record: " + userTeam.getWins() + "-" + userTeam.getLosses());
            System.out.println("   Cap Space: $" + String.format("%.2f", userTeam.getCapSpace()) + "M");
            System.out.println("==========================================");
            System.out.println("1. View Roster");
            System.out.println("2. Free Agency Market");
            System.out.println("3. Trade Center");
            System.out.println("4. Play Next Game");
            System.out.println("5. Exit Game");
            System.out.print("Choose option: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    showRoster();
                    break;
                case "2":
                    manageFreeAgency(scanner);
                    break;
                case "3":
                    manageTrades(scanner);
                    break;
                case "4":
                    Team opponent = getNextOpponent(currentWeek);
                    GameEngine.playInteractiveGame(userTeam, opponent, scanner);
                    currentWeek++;
                    break;
                case "5":
                    running = false;
                    System.out.println("Exiting simulator. Good luck next season!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        if (currentWeek > 5) {
            System.out.println("\n=== REGULAR SEASON COMPLETED ===");
            System.out.println("Final Record: " + userTeam.getWins() + " Wins - " + userTeam.getLosses() + " Losses");
        }
        scanner.close();
    }

    // ---------------- HELPER METHODS ----------------

    private static void initializeLeague() {
        // Create Teams
        Team kc = new Team("Kansas City Chiefs", "AFC West");
        Team sf = new Team("San Francisco 49ers", "NFC West");
        Team bal = new Team("Baltimore Ravens", "AFC North");
        Team phi = new Team("Philadelphia Eagles", "NFC East");

        // Starters for Chiefs
        Player mahomes = new Player("Patrick Mahomes", Position.QB, 28, 45.0, 4);
        mahomes.setAttribute("throwPower", 97); mahomes.setAttribute("accuracy", 96); mahomes.setAttribute("awareness", 98);
        kc.addPlayer(mahomes);

        Player kelce = new Player("Travis Kelce", Position.TE, 34, 14.0, 2);
        kelce.setAttribute("speed", 82); kelce.setAttribute("catching", 98); kelce.setAttribute("routeRunning", 96);
        kc.addPlayer(kelce);

        // Starters for 49ers
        Player purdy = new Player("Brock Purdy", Position.QB, 24, 1.0, 2);
        purdy.setAttribute("throwPower", 84); purdy.setAttribute("accuracy", 89); purdy.setAttribute("awareness", 88);
        sf.addPlayer(purdy);

        Player cmc = new Player("Christian McCaffrey", Position.RB, 27, 12.0, 3);
        cmc.setAttribute("speed", 92); cmc.setAttribute("elusiveness", 96); cmc.setAttribute("carrying", 94);
        sf.addPlayer(cmc);

        // Starters for Ravens
        Player lamar = new Player("Lamar Jackson", Position.QB, 27, 40.0, 4);
        lamar.setAttribute("throwPower", 90); lamar.setAttribute("accuracy", 86); lamar.setAttribute("awareness", 91);
        bal.addPlayer(lamar);

        // Starters for Eagles
        Player hurts = new Player("Jalen Hurts", Position.QB, 25, 30.0, 4);
        hurts.setAttribute("throwPower", 87); hurts.setAttribute("accuracy", 85); hurts.setAttribute("awareness", 89);
        phi.addPlayer(hurts);

        leagueTeams.add(kc);
        leagueTeams.add(sf);
        leagueTeams.add(bal);
        leagueTeams.add(phi);

        // Free Agent Pool
        Player tannehill = new Player("Ryan Tannehill", Position.QB, 35, 4.0, 1);
        tannehill.setAttribute("throwPower", 80); tannehill.setAttribute("accuracy", 78); tannehill.setAttribute("awareness", 80);
        freeAgentPool.add(tannehill);

        Player hunt = new Player("Kareem Hunt", Position.RB, 28, 3.0, 1);
        hunt.setAttribute("speed", 85); hunt.setAttribute("elusiveness", 82); hunt.setAttribute("carrying", 84);
        freeAgentPool.add(hunt);

        Player thomas = new Player("Michael Thomas", Position.WR, 31, 5.0, 1);
        thomas.setAttribute("speed", 82); thomas.setAttribute("catching", 88); thomas.setAttribute("routeRunning", 86);
        freeAgentPool.add(thomas);

        // Draft Rookie Pool
        Player rookie1 = new Player("Caleb Williams", Position.QB, 21, 3.5, 4);
        rookie1.setAttribute("throwPower", 92); rookie1.setAttribute("accuracy", 88); rookie1.setAttribute("awareness", 80);
        draftClass.add(rookie1);

        Player rookie2 = new Player("Marvin Harrison Jr.", Position.WR, 21, 3.0, 4);
        rookie2.setAttribute("speed", 94); rookie2.setAttribute("catching", 91); rookie2.setAttribute("routeRunning", 90);
        draftClass.add(rookie2);
    }

    private static void runDraft(Scanner scanner) {
        System.out.println("\n------------------------------------------");
        System.out.println("         OFFSEASON NFL DRAFT              ");
        System.out.println("------------------------------------------");
        System.out.println("Top Available Rookies:");
        for (int i = 0; i < draftClass.size(); i++) {
            System.out.println((i + 1) + ". " + draftClass.get(i));
        }
        System.out.print("Select rookie to draft (1-" + draftClass.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            Player drafted = draftClass.remove(choice);
            if (userTeam.addPlayer(drafted)) {
                System.out.println("SUCCESS! You drafted " + drafted.getName() + "!");
            } else {
                System.out.println("Could not sign drafted rookie due to cap space limits.");
            }
        } catch (Exception e) {
            System.out.println("Skipped draft selection.");
        }
    }

    private static void showRoster() {
        System.out.println("\n=== " + userTeam.getName() + " ROSTER ===");
        for (Player p : userTeam.getRoster()) {
            System.out.println(p);
        }
        System.out.println("Total Cap Space Remaining: $" + String.format("%.2f", userTeam.getCapSpace()) + "M");
    }

    private static void manageFreeAgency(Scanner scanner) {
        System.out.println("\n=== FREE AGENT MARKET ===");
        for (int i = 0; i < freeAgentPool.size(); i++) {
            System.out.println((i + 1) + ". " + freeAgentPool.get(i));
        }
        System.out.print("Enter number of player to sign (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= freeAgentPool.size()) {
                Player target = freeAgentPool.get(choice - 1);
                if (userTeam.addPlayer(target)) {
                    freeAgentPool.remove(target);
                    System.out.println("Successfully signed " + target.getName() + "!");
                } else {
                    System.out.println("Not enough cap space to sign " + target.getName() + "!");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid selection.");
        }
    }

    private static void manageTrades(Scanner scanner) {
        System.out.println("\n=== TRADE CENTER ===");
        System.out.println("Choose a team to trade with:");
        List<Team> otherTeams = new ArrayList<>();
        for (Team t : leagueTeams) {
            if (!t.equals(userTeam)) otherTeams.add(t);
        }
        for (int i = 0; i < otherTeams.size(); i++) {
            System.out.println((i + 1) + ". " + otherTeams.get(i).getName());
        }
        System.out.print("Select team (1-" + otherTeams.size() + "): ");
        try {
            int teamIdx = Integer.parseInt(scanner.nextLine()) - 1;
            Team tradePartner = otherTeams.get(teamIdx);

            System.out.println("\nTheir Roster:");
            for (int i = 0; i < tradePartner.getRoster().size(); i++) {
                System.out.println((i + 1) + ". " + tradePartner.getRoster().get(i));
            }
            System.out.print("Select player to acquire (1-" + tradePartner.getRoster().size() + "): ");
            int partnerPlayerIdx = Integer.parseInt(scanner.nextLine()) - 1;
            Player targetPlayer = tradePartner.getRoster().get(partnerPlayerIdx);

            System.out.println("\nYour Roster:");
            for (int i = 0; i < userTeam.getRoster().size(); i++) {
                System.out.println((i + 1) + ". " + userTeam.getRoster().get(i));
            }
            System.out.print("Select player to offer (1-" + userTeam.getRoster().size() + "): ");
            int userPlayerIdx = Integer.parseInt(scanner.nextLine()) - 1;
            Player offeredPlayer = userTeam.getRoster().get(userPlayerIdx);

            // Simple trade logic evaluation
            if (offeredPlayer.getOverallRating() >= targetPlayer.getOverallRating() - 3) {
                userTeam.removePlayer(offeredPlayer);
                tradePartner.removePlayer(targetPlayer);

                userTeam.addPlayer(targetPlayer);
                tradePartner.addPlayer(offeredPlayer);
                System.out.println("TRADE ACCEPTED! You traded " + offeredPlayer.getName() + " for " + targetPlayer.getName());
            } else {
                System.out.println("TRADE REJECTED! The " + tradePartner.getName() + " said the offer was not valuable enough.");
            }
        } catch (Exception e) {
            System.out.println("Trade cancelled or invalid selection.");
        }
    }

    private static Team getNextOpponent(int week) {
        for (Team t : leagueTeams) {
            if (!t.equals(userTeam)) {
                return t;
            }
        }
        return leagueTeams.get(0);
    }
}
