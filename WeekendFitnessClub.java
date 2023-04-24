package org.example;

import java.util.*;

public class WeekendFitnessClub {
    private static final int NUM_DAYS = 16;
    private static final int LESSONS_PER_DAY = 2;
    private static final int MAX_CAPACITY = 5;

    private static final String[] FITNESS_TYPES = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA"};
    private static final double[] PRICES = {15.0, 12.0, 10.0, 13.0};
//    private static final int[][] TIMETABLE = {
//            {0, 1, 2, 3}, {4, 5, 6, 7}, {0, 2, 4, 6}, {1, 3, 5, 7},
//            {0, 5, 2, 7}, {1, 6, 3, 4}, {0, 3, 4, 6}, {1, 2, 5, 7}
//    };

    private static int[][] bookings = new int[NUM_DAYS][LESSONS_PER_DAY * NUM_DAYS];
    private static double[] incomes = new double[FITNESS_TYPES.length];
    private static Review review = new Review(new double[FITNESS_TYPES.length][2]);


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("---------------------------------------------------");
            System.out.println("Welcome to the Weekend Fitness Club booking system!");
            System.out.println("---------------------------------------------------");
            System.out.println("1. Book a group fitness lesson");
            System.out.println("2. Change/Cancel a booking");
            System.out.println("3. Attend a lesson");
            System.out.println("4. Monthly lesson report");
            System.out.println("5. Monthly champion fitness type report");
            System.out.println("0. Exit");
            System.out.print("Please select an option:");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    bookLesson(scanner);
                    break;
                case 2:
                    changeOrCancelBooking(scanner);
                    break;
                case 3:
                    attendLesson(scanner);
                    break;
                case 4:
                    generateMonthlyLessonReport();
                    break;
                case 5:
                    generateMonthlyFitnessTypeReport();
                    break;
                case 0:
                    System.out.println("Thank you for using the Weekend Fitness Club booking system!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void bookLesson(Scanner scanner) {
        System.out.print("Please enter your customer ID:");
        int customerId = scanner.nextInt();
        System.out.println("---------------------------------------------");
        System.out.println("Please select the way to check the timetable:");
        System.out.println("---------------------------------------------");
        System.out.println("1. By day (Saturday or Sunday)");
        System.out.println("2. By fitness type");

        int option = scanner.nextInt();
        String fitnessTypeChoice = "";
        int[] availableLessons ;
        int[] availableLessonsType = new int[NUM_DAYS/2];
        if (option == 1) {
//            System.out.println("Please enter the day (1-8):");
//            int day = scanner.nextInt() - 1;
            int day = 1;
            availableLessons = getAvailableLessonsForDay(day);
        } else if (option == 2) {
            System.out.println("Please enter the fitness type:");
            System.out.println("(Spin,Yoga,BodySculpt,Zumba)");
            fitnessTypeChoice += scanner.next().toUpperCase();
//            availableLessons = getAvailableLessonsForFitnessType(fitnessTypeChoice);
            int day=1;
            availableLessons = getAvailableLessonsForDay(day);
        } else {
            System.out.println("Invalid option. Please try again.");
            return;
        }

        if (availableLessons.length == 0) {
            System.out.println("Sorry, there are no available lessons for your selected option.");
            return;
        }


        System.out.println("Available lessons: ");
        System.out.println("-------------------");
        if(option == 1) {
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % FITNESS_TYPES.length;
                String fitnessType = FITNESS_TYPES[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }

        //to print lessons of a type
        int typeIndex = getFitnessTypeIndex(fitnessTypeChoice);
//        System.out.println(fitnessTypeChoice + typeIndex);
        if(option==2) {
            for (int i = typeIndex; i < availableLessons.length; i+=FITNESS_TYPES.length) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % FITNESS_TYPES.length;
                String fitnessType = FITNESS_TYPES[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }

        System.out.print("Please select a lesson :");
        int lessonChoice = scanner.nextInt();

        int lessonIndex = availableLessons[lessonChoice - 1];
        int day = lessonIndex % LESSONS_PER_DAY;
        int lesson = lessonIndex % FITNESS_TYPES.length;
        String fitnessType = FITNESS_TYPES[lesson];


        System.out.println("You have selected " + fitnessType + " on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        if (bookings[day][lessonIndex] >= MAX_CAPACITY) {
            System.out.println("Sorry, the lesson is fully booked.");
            return;
        }

        int fitnessTypeIndex=0;
        if(option == 1) {
            bookings[day][lessonIndex]++;
            fitnessTypeIndex = getFitnessTypeIndex(fitnessType);
        }
        if(option == 2){
            bookings[day][getFitnessTypeIndex(fitnessTypeChoice)]++;
            fitnessTypeIndex = getFitnessTypeIndex(fitnessTypeChoice);
        }
//        incomes[fitnessTypeIndex] += PRICES[lesson];

        System.out.println("book");
        System.out.println(day);
        System.out.println(lessonIndex);
        System.out.println(lesson);
        System.out.println(customerId);

        System.out.println("Booking successful! Your booking ID is " + generateBookingId(day, lessonIndex, customerId));
    }

    private static void changeOrCancelBooking(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId = scanner.nextInt();

        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % FITNESS_TYPES.length;
        int customerId = bookingDetails[2];

        if(bookings[day][lessonIndex] == 0){
            System.out.println("Booking Id not valid!");
            return;
        }

        String fitnessType = FITNESS_TYPES[lesson];

        System.out.println("You have booked " + fitnessType + " on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        System.out.println("Please select an option:");
        System.out.println("1. Change booking to another lesson");
        System.out.println("2. Cancel booking");

        int option = scanner.nextInt();

        if (option == 1) {
            int[] availableLessons = getAvailableLessonsForDay(1);

            if (availableLessons.length == 0) {
                System.out.println("Sorry, there are no available lessons for your selected option.");
                return;
            }

            System.out.println("Available lessons: ");
            System.out.println("___________________");
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndexA = availableLessons[i];
                int dayA = i % 2;
                int lessonA = lessonIndexA % FITNESS_TYPES.length;
                String fitnessTypeA = FITNESS_TYPES[lessonA];

                System.out.println((i + 1) + ". " + fitnessTypeA + " on " + getDayOfWeek(dayA % 2) +
                        " at " + getTimeOfDay(lessonA) + " with a price of $" + PRICES[lessonA]);
            }

            System.out.println("Please select a new lesson (1-" + availableLessons.length + "):");
            int newLessonChoice = scanner.nextInt();
            int newLessonIndex = availableLessons[newLessonChoice - 1];
            int newDay = newLessonIndex % LESSONS_PER_DAY;
            int newLesson = newLessonIndex % FITNESS_TYPES.length;
            System.out.println("new kessom" + newLessonIndex);

            System.out.println("You have selected " + FITNESS_TYPES[newLesson] + " on " + getDayOfWeek(newDay%2) +
                    " at " + getTimeOfDay(newLesson) + " with a price of $" + PRICES[newLesson]);

            bookings[day][lessonIndex]--;
            bookings[newDay][newLessonIndex]++;
            System.out.println("Booking changed successfully! Your new booking ID is " + generateBookingId(newDay, newLessonIndex, customerId));
        } else if (option == 2) {
            bookings[day][lessonIndex]--;
            int fitnessTypeIndex = getFitnessTypeIndex(fitnessType);
            incomes[fitnessTypeIndex] -= PRICES[lesson];
            System.out.println("Booking cancelled successfully!");
        } else {
            System.out.println("Invalid option. Please try again.");
        }
    }


    private static void attendLesson(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId;
        try {
            bookingId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid booking ID.");
            scanner.next();
            return;
        }

        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % FITNESS_TYPES.length;
        int customerId = bookingDetails[2];

        if (bookings[day][lessonIndex] == 0) {
            System.out.println("Sorry, this lesson is not booked by anyone.");
            return;
        }

//        if (customerId != getCustomerId(day, lesson, bookings[day][lesson])) {
//            System.out.println("Sorry, you cannot attend a lesson booked by someone else.");
//            return;
//        }

        String fitnessType = FITNESS_TYPES[lesson];
        System.out.println("Welcome to the " + fitnessType + " class on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson));

        // Prompt the user for their rating and feedback
        System.out.println("Please rate the lesson (1-5):");
        int rating = scanner.nextInt();
        scanner.nextLine(); // consume the remaining newline character
        System.out.println("Please provide your feedback:");
        String feedback = scanner.nextLine();

        System.out.println("Thank you for your rating of " + rating + " and feedback: " + feedback);

//        bookings[day][lessonIndex]+=1;
        double income = PRICES[lesson];
        incomes[lesson] += income;
        System.out.println("Your account will be charged $" + income + " for attending the lesson.");
        System.out.println();

        review.addAverageRating(lesson, rating);
    }


    public static void generateMonthlyLessonReport() {
        int totalBookings = 0;
        double totalIncome = 0;

        System.out.println();
        System.out.println("----------------------");
        System.out.println("Monthly lesson report:");
        System.out.println("----------------------");

        for (int i = 0; i < NUM_DAYS; i++) {
//            System.out.println("Week " + (i+1) + ":");
            for (int j = 0; j < LESSONS_PER_DAY * NUM_DAYS; j++) {
                if (bookings[i][j] > 0 && review.getAverageRating(j% FITNESS_TYPES.length) > 0) {
//                    System.out.println(j);
                    int lesson = j % FITNESS_TYPES.length;
                    int fitnessTypeIndex = j % FITNESS_TYPES.length;
                    String fitnessType = FITNESS_TYPES[fitnessTypeIndex];
                    double income = PRICES[fitnessTypeIndex] * bookings[i][j];
                    totalBookings += bookings[i][j];
                    totalIncome += income;
//                    System.out.println("-----------------------------------------------------");
//                    System.out.println("Lesson      |    No of Customers     |     Avg Rating" );
//                    System.out.println(fitnessType + "");
                    System.out.println(fitnessType + " : No of Customers " + bookings[i][j] +
                            " with Avg Rating "+ review.getAverageRating(fitnessTypeIndex));
                }
            }
        }

        System.out.println();
        System.out.println("Total bookings: " + totalBookings);
        System.out.println("Total income: $" + totalIncome);
        System.out.println();
    }

    public static void generateMonthlyFitnessTypeReport() {
        System.out.println();
        System.out.println("-------------------------------------");
        System.out.println("Monthly champion fitness type report:");
        System.out.println("-------------------------------------");
        System.out.println();
        int maxIndex = 0;
        double maxIncome = incomes[0];

        for (int i = 0; i < FITNESS_TYPES.length; i++) {
            if (incomes[i] > maxIncome) {
                maxIndex = i;
                maxIncome = incomes[i];
            }
            System.out.println(FITNESS_TYPES[i] + " : Total income - " + incomes[i] );
        }

        System.out.println();
        if(maxIncome>0) {
            System.out.println("The champion fitness type for this month is " + FITNESS_TYPES[maxIndex] +
                    " with a total income of $" + maxIncome);
        }else {
            System.out.println("No income as of now!");
        }
        System.out.println();
    }

    private static int[] getAvailableLessonsForDay(int day) {
        int[] availableLessons = new int[LESSONS_PER_DAY * NUM_DAYS];
        int index = 0;
        for (int i = 0; i < LESSONS_PER_DAY * NUM_DAYS; i++) {
            if (bookings[day][i] < MAX_CAPACITY) {
                availableLessons[index] = i;
                index++;
            }
        }

        return Arrays.copyOfRange(availableLessons, 0, index);
    }
    private static int[] getAvailableLessonsForFitnessType(String fitnessType) {
        int fitnessTypeIndex = getFitnessTypeIndex(fitnessType);

        if (fitnessTypeIndex == -1) {
            return new int[0];
        }

        List<Integer> availableLessons = new ArrayList<>();
        for (int day = 0; day < NUM_DAYS; day++) {
            for (int lesson = 0; lesson < LESSONS_PER_DAY*NUM_DAYS; lesson++) {
                int lessonIndex = day * LESSONS_PER_DAY + lesson;
                if (lessonIndex < FITNESS_TYPES.length && FITNESS_TYPES[lessonIndex / 2].equals(fitnessType) && bookings[day][lessonIndex] < MAX_CAPACITY) {
                    availableLessons.add(lessonIndex);
                }
            }
        }

        int[] result = new int[availableLessons.size()];
        for (int i = 0; i < availableLessons.size(); i++) {
            result[i] = availableLessons.get(i);
        }

        return result;
    }

    //  method to get the index of a fitness type in the FITNESS_TYPES array
    private static int getFitnessTypeIndex(String fitnessType) {
        for (int i = 0; i < FITNESS_TYPES.length; i++) {
            if (FITNESS_TYPES[i].equals(fitnessType)) {
                return i;
            }
        }
        return -1;
    }

//    private static int getCustomerId(int day, int lesson, int booking) {
//        return bookings[day][lesson][booking][0];
//    }


    //  method to generate a booking ID from the day, lesson, and customer ID
    private static int generateBookingId(int day, int lesson, int customerId) {
        return day * 10000 + lesson * 100 + customerId;
    }


    //  method to parse a booking ID into its day, lesson, and customer ID components
    private static int[] parseBookingId(int bookingId) {
        int day = bookingId / 10000;
        int lesson = (bookingId % 10000) / 100;
        int customerId = bookingId % 10;

        System.out.println(day);
        System.out.println(lesson);
        System.out.println(customerId);

        if (day < 0 || day >= NUM_DAYS || lesson < 0 || lesson >= (NUM_DAYS * LESSONS_PER_DAY) ||
                customerId <= 0 || customerId > MAX_CAPACITY) {
            System.out.println("Invalid booking ID.");
            return null;
        }

        return new int[] {day, lesson, customerId};
    }

    //  method to get the day of the week as a string (e.g. "Saturday")
    private static String getDayOfWeek(int day) {
        switch (day) {
            case 0:
                return "Saturday";
            case 1:
                return "Sunday";
            default:
                return "Invalid day";
        }
    }

    //  method to get the time of day as a string (e.g. "10:30am")
    private static String getTimeOfDay(int lesson) {
        switch (lesson) {
            case 0:
                return "9:00am";
            case 1:
                return "10:30am";
            case 2:
                return "12:00pm";
            case 3:
                return "1:30pm";
            default:
                return "Invalid lesson";
        }
    }
}
