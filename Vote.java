/* Online Voting System */

import java.util.Scanner;
import java.util.InputMismatchException;

import java.io.FileWriter;
import java.io.BufferedWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;

/* Exception Handling */
class NegativeNumberException extends RuntimeException {
    NegativeNumberException() {
        super("Negative Argument is not Allowed.");
    }
}

class UnderAgeException extends RuntimeException {
    UnderAgeException() {
        super("You are Below 18.");
    }
}

class NotPossibleAgeException extends RuntimeException {
    NotPossibleAgeException() {
        super("This Age is not Possible.");
    }
}

class Voting_System {
    public static void main(String[] args) throws Exception {

        System.out.println();
        System.out.println("********************************");
        System.out.println("Welcome to Online Voting System.");
        System.out.println("********************************");

        Scanner sc = new Scanner(System.in);
        Queue queue = new Queue(100);

        /* For Connect to Database */
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Voting_System", "root", "");

        int choice;
        try {
            do {
            
                System.out.println();
                System.out.println("Press 1 to Vote");
                System.out.println("Press 2 to View Candidate Details");
                System.out.println("Press 3 to View Result");
                System.out.println("Press 4 to Exit.");
                System.out.println();
                System.out.print("Enter your choice :- ");
                choice = sc.nextInt();
    
                switch(choice) {
                    case 1:
                        try {
                
                            System.out.println();
                            System.out.print("Enter your Age :- ");
                            int v_age = sc.nextInt();
                            System.out.println();
            
                            /* Check Voters Age is Valid or not */
                            if(v_age < 0)
                                throw new NegativeNumberException();
                            else if(v_age < 18)
                                throw new UnderAgeException();
                            else if(v_age > 100)
                                throw new NotPossibleAgeException();
                            else if(v_age > 18 && v_age < 100) {
                    
                                System.out.println("Valid Age.");
                                System.out.println();
                
                                System.out.println("Enter your Details :- ");
                                System.out.println();
                
                                System.out.print("Enter your First Name :- ");
                                String v_firstname = sc.next();
                                    
                                System.out.print("Enter your Last Name :- ");
                                String v_lastname = sc.next();
                    
                                /* Generate Login I'd */
                                String login_id = v_firstname.toLowerCase() + v_lastname.toLowerCase() + "@1234"; 
                    
                                /* For check Voter is Alreday Voted or not */
                                Statement st = con.createStatement();
                                String sql = "select * from votes";
                                ResultSet rs = st.executeQuery(sql);
                    
                                while(rs.next()) {
                                    if(rs.getString(2).equals(login_id)) {
                                        System.out.println("You had alreday Voted.");
                                        System.exit(0);
                                    } else {
                                        /* if Voter is not Voted then they insert into Queue */
                                        queue.enqueue(v_age);
                                    }
                                }
                                /* Consume new Line */
                                sc.nextLine();
                    
                                /* For Enter Another Details */
                                System.out.print("Enter your Address :- ");
                                String v_address = sc.nextLine();

                                // fetch details in if else
                    
                                System.out.print("Enter your Area :- ");
                                String v_area = sc.nextLine();
                    
                                System.out.print("Enter your City Name :- ");
                                String v_city = sc.nextLine();
                    
                                System.out.print("Enter your State Name :- ");
                                String v_state = sc.nextLine();
                                System.out.println();
                    
                                /* For Voter Details insert into Database */
                                String sql1 = "insert into voter_details (v_firstname, v_lastname, v_age, v_address, v_area, v_city, v_state, login_id) values (?,?,?,?,?,?,?,?)";
                                PreparedStatement pst1 = con.prepareStatement(sql1);
                    
                                pst1.setString(1, v_firstname);
                                pst1.setString(2, v_lastname);
                                pst1.setInt(3, v_age);
                                pst1.setString(4, v_address);
                                pst1.setString(5, v_area);
                                pst1.setString(6, v_city);
                                pst1.setString(7, v_state);
                                pst1.setString(8, login_id);
                
                                pst1.executeUpdate();
                    
                                /* For Voter Details Write into File */
                                FileWriter fw1= new FileWriter("Voter_Details.txt");
                                BufferedWriter bw1= new BufferedWriter(fw1);
                    
                                bw1.write(login_id + " ");
                                bw1.write(v_age + " ");
                                bw1.write(v_area + " ");
                                bw1.write(v_city + " ");
                                bw1.write(v_state + " ");
                                bw1.newLine();
                                bw1.close();
                    
                                /* For Check How many Candidates are in Voters Area */
                                Statement st1 = con.createStatement();
                                String sql2 = "select * from Candidate_Details";
                                ResultSet rs1 = st1.executeQuery(sql2);
                    
                                int i = 1;
                                boolean flag = false;
                                while(rs1.next()) {
                    
                                    if(rs1.getString(3).equalsIgnoreCase(v_area) && rs1.getString(4).equalsIgnoreCase(v_city) && rs1.getString(5).equalsIgnoreCase(v_state)) {
                                        System.out.println(i + " to Vote " + rs1.getString(2)+ " " + rs1.getString(6));
                                        flag = true;
                                        i++;
                                    }
                                }
                    
                                if(flag == false) 
                                    System.out.println("No Candidate in your Area.");
                    
                                System.out.println();
                                System.out.print("Enter Party Name to Vote :- ");
                                String vote = sc.nextLine();
                    
                                System.out.println();
                                System.out.println("Your Vote has Saved.");
                                System.out.println();
                    
                                /*When Voters have voted Successfully then they are remove from Queue */
                                queue.dequeue();
                    
                                /* For insert Vote in Database */
                                String sql3 = "insert into Votes (v_name, v_login_id, v_area, party_name) values (?,?,?,?)";
                                PreparedStatement pst2 = con.prepareStatement(sql3);
                                            
                                pst2.setString(1, v_firstname.concat(" " + v_lastname));
                                pst2.setString(2, login_id);
                                pst2.setString(3, v_area);
                                pst2.setString(4, vote);
                    
                                pst2.executeUpdate();
                    
                                /* For write Votes in File */
                                FileWriter fw2 = new FileWriter("Votes.txt");
                                BufferedWriter bw2 = new BufferedWriter(fw2);
                    
                                bw2.write(vote);
                                bw2.newLine();
                                bw2.close();
                            }
                    } catch (NegativeNumberException e) {
            
                        System.out.println(e.getMessage());
                        System.out.println();
            
                    } catch(InputMismatchException e) {
            
                        System.out.println();
                        System.out.println("Invalid Details.");
                        System.out.println();
            
                    } catch(NotPossibleAgeException e) {
            
                        System.out.println(e.getMessage());
                        System.out.println();
            
                    } catch (UnderAgeException e) {
            
                        System.out.println(e.getMessage());
                        System.out.println();
                    }
                    break;
                    case 2: 
                        System.out.println();
                        System.out.println("BJP Candidate Details :- ");
                        
                        /* For View Candidate Details */
                        String sql4 = "{call get_BJPCandidate}";
                        CallableStatement cst1 = con.prepareCall(sql4);
                        ResultSet rs2 = cst1.executeQuery();

                        while(rs2.next()) {
                            System.out.print("Id :- " + rs2.getInt(1) + " ");
                            System.out.print("Name :- " + rs2.getString(2) + " ");
                            System.out.print("Area :- " + rs2.getString(3) + " ");
                            System.out.println("Party Name :- " + rs2.getString(4));
                        }

                        System.out.println();
                        System.out.println("CNG Candidate Details :- ");

                        String sql5 = "{call get_CNGCandidate}";
                        CallableStatement cst2 = con.prepareCall(sql5);
                        ResultSet rs3 = cst2.executeQuery();

                        while(rs3.next()) {
                            System.out.print("Id :- " + rs3.getInt(1) + " ");
                            System.out.print("Name :- " + rs3.getString(2) + " ");
                            System.out.print("Area :- " + rs3.getString(3) + " ");
                            System.out.println("Party Name :- " + rs3.getString(4));
                        }

                        System.out.println();
                        System.out.println("AAM Candidate Details :- ");

                        String sql6 = "{call get_AAMCandidate}";
                        CallableStatement cst3 = con.prepareCall(sql6);
                        ResultSet rs4 = cst3.executeQuery();

                        while(rs4.next()) {
                            System.out.print("Id :- " + rs4.getInt(1) + " ");
                            System.out.print("Name :- " + rs4.getString(2) + " ");
                            System.out.print("Area :- " + rs4.getString(3) + " ");
                            System.out.println("Party Name :- " + rs4.getString(4));
                        }
                        break;
                    case 3:
                        String sql7 = "Select * from votes";
                        PreparedStatement pst6 = con.prepareStatement(sql7);
                        ResultSet rs5 = pst6.executeQuery();

                        int bjp_count = 0;
                        int cng_count = 0;
                        int aam_count = 0;

                        while(rs5.next()) {
                            if(rs5.getString(5).equals("BJP"))
                                bjp_count++;
                            else if(rs5.getString(5).equals("CNG"))
                                cng_count++;
                            else if(rs5.getString(5).equals("AAM"))
                                aam_count++;
                        }

                        System.out.println("BJP Vote = " + bjp_count);
                        System.out.println("CNG Vote = " + cng_count);
                        System.out.println("AAM Vote = " + aam_count);

                        break;
                    case 4: 
                        System.out.println();
                        System.out.println("You selected Exit.");
                        System.out.println("Thank You.");
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid Choice.");
                        break;
                }
            } while (choice != 4);
        } catch(InputMismatchException e) {

            System.out.println();
            System.out.println("Invalid Choice.");
            System.out.println();
        }
    }
}

/* Data Structure */

class Queue {

    int front; 
    int rear;
    int capacity;
    int v_arr[];

    Queue(int size) {
        front = -1;
        rear = -1;
        capacity = size;
        v_arr = new int[capacity];
    }

    void enqueue(int x) {

        if(rear == capacity - 1)
            System.out.println("Queue is Full.");
        else {
            if(front == -1)
                front = 0;
            else {
                rear = rear + 1;
                v_arr[rear] = x;
            }
        }
    }

    void dequeue() {

        if(front == -1)
            return;
        else {
            if(front == rear) {
                front = -1;
                rear = -1;
            } else 
                front = front + 1;
        }
    }

    void display() {

        if(front == -1)
            return;
        else {
            for(int i = front; i < rear; i++)
                System.out.print(v_arr[i] + " ");
        }
    }
}