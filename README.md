# Expense Tracker - Splitwise-like Android App

A complete Android application built with Java that allows users to split expenses among friends, similar to Splitwise. The app uses SQLite for local data storage and includes all the features required for a comprehensive expense tracking system.

## Features

### 🏗️ Architecture and Activity Life Cycle (2 Marks)
- **Multiple Activities**: HomeActivity, GroupDetailsActivity, AddExpenseActivity
- **Activity Lifecycle Methods**: Implemented onCreate(), onStart(), onPause(), and onDestroy()
- **Data Persistence**: SQLite database ensures data persistence across app sessions
- **Intent Navigation**: Clean navigation flow between activities

### 🎨 UI Design (3 Marks)
- **Home Screen**: Displays all groups with total expense summary using RecyclerView
- **Group Details Screen**: Shows expenses and member balances with TabLayout
- **Add Expense Screen**: Complete form with validation and DatePicker
- **Layout Types**: 
  - ConstraintLayout for responsive design
  - LinearLayout for vertical stacking
  - CardView for modern UI presentation
  - RecyclerView for efficient list display

### ⚡ UI Functionality (4 Marks)
- **Input Validation**: Comprehensive validation for all form fields
- **Automatic Balance Updates**: Real-time balance calculations
- **Portrait/Landscape Support**: Responsive design using ConstraintLayout
- **Clean Navigation**: Intent-based navigation between activities
- **Date Selection**: DatePicker integration for expense dates

### 🗄️ Database Management (5 Marks)
- **SQLite Database**: Local storage with three main tables:
  - `groups`: Group information
  - `expenses`: Expense details with relationships
  - `members`: Member information and balances
- **Full CRUD Operations**:
  - Create: Add groups, expenses, and members
  - Read: Display all data with relationships
  - Update: Edit existing records
  - Delete: Remove records with cascade handling
- **Automatic Balance Calculation**: Real-time balance updates when expenses are added/modified

### 🛠️ Tools Used (4 Marks)
- **SQLite**: Local database for storing expense and group details
- **RecyclerView**: Efficient display of expenses and groups
- **CardView**: Modern UI presentation of each expense/group
- **NotificationManager**: Settlement reminders and expense notifications
- **SharedPreferences**: App settings and user preferences
- **ViewPager2**: Tab-based navigation in group details
- **Material Design**: Modern UI components and theming

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/expensetracker/
│   │   ├── HomeActivity.java              # Main activity with groups list
│   │   ├── GroupDetailsActivity.java      # Group details with tabs
│   │   ├── AddExpenseActivity.java        # Add expense form
│   │   ├── models/                        # Data models
│   │   │   ├── Group.java
│   │   │   ├── Expense.java
│   │   │   ├── Member.java
│   │   │   └── Balance.java
│   │   ├── database/                      # Database management
│   │   │   └── DatabaseHelper.java
│   │   ├── adapters/                      # RecyclerView adapters
│   │   │   ├── GroupAdapter.java
│   │   │   ├── ExpenseAdapter.java
│   │   │   └── BalanceAdapter.java
│   │   ├── fragments/                     # UI fragments
│   │   │   ├── ExpensesFragment.java
│   │   │   └── BalancesFragment.java
│   │   └── utils/                         # Utility classes
│   │       ├── NotificationHelper.java
│   │       └── DateUtils.java
│   └── res/
│       ├── layout/                        # XML layouts
│       │   ├── activity_main.xml
│       │   ├── activity_group_details.xml
│       │   ├── activity_add_expense.xml
│       │   ├── item_group.xml
│       │   ├── item_expense.xml
│       │   ├── item_balance.xml
│       │   ├── fragment_expenses.xml
│       │   └── fragment_balances.xml
│       └── values/
│           ├── colors.xml
│           ├── strings.xml
│           └── themes.xml
```

## Installation and Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API level 24 or higher
- Java 11 or later

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Expense_App
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the Expense_App folder and select it

3. **Sync Project**
   - Android Studio will automatically sync the project
   - Wait for Gradle sync to complete

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

### Running the App

1. **On Emulator**
   - Create an Android Virtual Device (AVD)
   - Run the app using the play button in Android Studio

2. **On Physical Device**
   - Enable Developer Options and USB Debugging
   - Connect device via USB
   - Run the app directly on the device

## Testing

### Manual Testing Steps

1. **Home Screen Testing**
   - Launch the app
   - Verify groups are displayed in RecyclerView
   - Test adding new groups using the FAB
   - Check empty state when no groups exist

2. **Group Details Testing**
   - Tap on a group to open details
   - Verify expenses and balances tabs work
   - Test navigation between tabs

3. **Add Expense Testing**
   - Tap the + button in group details
   - Fill out the expense form
   - Test validation (empty fields, invalid amounts)
   - Verify date picker functionality
   - Save expense and verify it appears in the list

4. **Database Testing**
   - Add multiple expenses
   - Verify balance calculations are correct
   - Test data persistence by closing and reopening the app

5. **Notification Testing**
   - Add new groups and expenses
   - Verify notifications appear
   - Test notification actions

### Test Cases

| Test Case | Expected Result |
|-----------|----------------|
| Add Group | Group appears in home screen list |
| Add Expense | Expense appears in group details |
| Balance Calculation | Balances update correctly based on expenses |
| Data Persistence | Data remains after app restart |
| Input Validation | Error messages for invalid inputs |
| Date Selection | Date picker opens and updates field |
| Navigation | Smooth transitions between activities |

## Screenshots

### Home Screen
- Displays all groups with total expenses
- Floating action button to add new groups
- Empty state when no groups exist

### Group Details Screen
- Tab-based navigation (Expenses/Balances)
- List of all expenses with details
- Member balances showing who owes what

### Add Expense Screen
- Form with expense name, amount, payer
- Date picker for expense date
- Participants list and description
- Input validation and error handling

## Key Features Implemented

### ✅ Architecture Requirements
- Multiple activities with proper lifecycle management
- Data persistence using SQLite
- Clean navigation using Intents

### ✅ UI/UX Features
- Modern Material Design interface
- Responsive layouts supporting portrait/landscape
- CardView and RecyclerView for clean presentation
- TabLayout for organized content display

### ✅ Functionality
- Complete CRUD operations for all entities
- Real-time balance calculations
- Input validation and error handling
- Date picker integration

### ✅ Database Management
- SQLite with proper table relationships
- Foreign key constraints
- Automatic balance updates
- Data integrity maintenance

### ✅ Additional Features
- Push notifications for important events
- SharedPreferences for app settings
- Sample data creation for first-time users
- Comprehensive error handling

## Technical Implementation

### Database Schema
```sql
-- Groups table
CREATE TABLE groups (
    group_id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_name TEXT NOT NULL,
    description TEXT,
    created_date TEXT
);

-- Expenses table
CREATE TABLE expenses (
    expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER NOT NULL,
    expense_name TEXT NOT NULL,
    amount REAL NOT NULL,
    payer TEXT NOT NULL,
    participants TEXT NOT NULL,
    date TEXT NOT NULL,
    description TEXT,
    category TEXT,
    FOREIGN KEY(group_id) REFERENCES groups(group_id)
);

-- Members table
CREATE TABLE members (
    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER NOT NULL,
    member_name TEXT NOT NULL,
    email TEXT,
    total_owed REAL DEFAULT 0,
    total_owing REAL DEFAULT 0,
    balance REAL DEFAULT 0,
    FOREIGN KEY(group_id) REFERENCES groups(group_id)
);
```

### Balance Calculation Logic
The app automatically calculates balances when expenses are added:
1. Parse participants from comma-separated string
2. Calculate amount per person (total amount / number of participants)
3. Update payer's "total_owing" (amount they should get back)
4. Update other participants' "total_owed" (amount they owe)
5. Calculate final balance (total_owed - total_owing)

## Future Enhancements

- User authentication and cloud sync
- Receipt photo capture
- Export to PDF/Excel
- Multiple currency support
- Recurring expenses
- Expense categories with icons
- Advanced reporting and analytics

## Conclusion

This Expense Tracker app successfully implements all the required features for a Splitwise-like application. It demonstrates proper Android development practices, clean architecture, and comprehensive functionality. The app is ready for production use and can be easily extended with additional features.

## License

This project is created for educational purposes as part of a mobile application development course.
