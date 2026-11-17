
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class GroupService {

    private final String groupName;
    private final Set<String> groupMembersNormalized;
    private final List<String> memberList;

    public GroupService(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }

        this.groupName = groupName.trim();
        this.groupMembersNormalized = new HashSet<>();
        this.memberList = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getMemberList() {
        return Collections.unmodifiableList(memberList);
    }

    // CREATE
    public boolean addMember(String name) {

        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nError: Member name cannot be empty.");
            return false;
        }

        String trimmedName = name.trim();
        String key = trimmedName.toLowerCase(Locale.ROOT);

        if (!groupMembersNormalized.add(key)) {
            System.out.println("\nError: This member already exists.");
            return false;
        }

        memberList.add(trimmedName);

        System.out.println("\nSuccess: Member added successfully!");
        return true;
    }

    // READ
    public void displayMembers() {

        System.out.println("\n==================================================");
        System.out.println("                  MEMBER DIRECTORY");
        System.out.println("==================================================");

        System.out.println("Group Name    : " + groupName);
        System.out.println("Total Members : " + memberList.size());

        System.out.println("--------------------------------------------------");

        if (memberList.isEmpty()) {
            System.out.println("No members have been added yet.");
        } else {
            System.out.printf("%-10s %-25s %-12s%n",
                    "MEMBER ID", "MEMBER NAME", "STATUS");

            System.out.println("--------------------------------------------------");

            for (int i = 0; i < memberList.size(); i++) {
                System.out.printf("%-10d %-25s %-12s%n",
                        i + 1, memberList.get(i), "Active");
            }
        }

        System.out.println("==================================================");
    }

    // UPDATE
    public boolean updateMember(String oldName, String newName) {

        if (oldName == null || oldName.trim().isEmpty()
                || newName == null || newName.trim().isEmpty()) {
            System.out.println("\nError: Names cannot be empty.");
            return false;
        }

        String oldTrimmed = oldName.trim();
        String newTrimmed = newName.trim();

        String oldKey = oldTrimmed.toLowerCase(Locale.ROOT);
        String newKey = newTrimmed.toLowerCase(Locale.ROOT);

        if (!groupMembersNormalized.contains(oldKey)) {
            System.out.println("\nError: Member not found.");
            return false;
        }

        if (groupMembersNormalized.contains(newKey)
                && !oldKey.equals(newKey)) {
            System.out.println("\nError: New name already exists.");
            return false;
        }

        groupMembersNormalized.remove(oldKey);
        groupMembersNormalized.add(newKey);

        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).equalsIgnoreCase(oldTrimmed)) {
                memberList.set(i, newTrimmed);
                break;
            }
        }

        System.out.println("\nSuccess: Member updated successfully!");
        return true;
    }

    // DELETE
    public boolean removeMember(String name) {

        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nError: Name cannot be empty.");
            return false;
        }

        String trimmedName = name.trim();
        String key = trimmedName.toLowerCase(Locale.ROOT);

        if (!groupMembersNormalized.remove(key)) {
            System.out.println("\nError: Member not found.");
            return false;
        }

        memberList.removeIf(
                member -> member.equalsIgnoreCase(trimmedName)
        );

        System.out.println("\nSuccess: Member removed successfully!");
        return true;
    }

    // GROUP INFORMATION
    public void displayGroupInfo() {

        System.out.println("\n==================================================");
        System.out.println("                 GROUP INFORMATION");
        System.out.println("==================================================");

        System.out.println("Group Name       : " + groupName);
        System.out.println("Group Status     : "
                + (memberList.isEmpty() ? "Waiting for Members" : "Active"));
        System.out.println("Total Members    : " + memberList.size());

        System.out.println("==================================================");
    }

    // MAIN MENU
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        GroupService group = new GroupService("Apartment 4B");

        int choice = 0;

        do {

            System.out.println("\n\n");
            System.out.println("==================================================");
            System.out.println("             SMART EXPENSE SPLITTER");
            System.out.println("                  GROUP MODULE");
            System.out.println("==================================================");

            System.out.println(" Group Name       : " + group.getGroupName());
            System.out.println(" Total Members    : " + group.getMemberList().size());

            System.out.println("==================================================");
            System.out.println("                    MAIN MENU");
            System.out.println("==================================================");

            System.out.println("  1. Add New Member");
            System.out.println("  2. View All Members");
            System.out.println("  3. Update Member");
            System.out.println("  4. Remove Member");
            System.out.println("  5. View Group Information");
            System.out.println("  6. Exit");

            System.out.println("==================================================");
            System.out.print(" Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nInvalid input! Enter a number.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("\nEnter member name: ");
                    String name = sc.nextLine();
                    group.addMember(name);
                    break;

                case 2:
                    group.displayMembers();
                    break;

                case 3:
                    System.out.print("\nEnter existing member name: ");
                    String oldName = sc.nextLine();

                    System.out.print("Enter new member name: ");
                    String newName = sc.nextLine();

                    group.updateMember(oldName, newName);
                    break;

                case 4:
                    System.out.print("\nEnter member name to remove: ");
                    String removeName = sc.nextLine();
                    group.removeMember(removeName);
                    break;

                case 5:
                    group.displayGroupInfo();
                    break;

                case 6:
                    System.out.println("\nThank you for using Group Management!");
                    break;

                default:
                    System.out.println("\nInvalid choice! Select 1-6.");
            }

            if (choice != 6) {
                System.out.println("\nPress Enter to return to the main menu...");
                sc.nextLine();
            }

        } while (choice != 6);

        sc.close();
    }
}
