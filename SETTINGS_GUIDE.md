# ⚙️ Settings Page Guide

## 🎯 Overview

The Settings page provides comprehensive control over the Expense Tracker app's behavior, appearance, and data management. Access it through the menu (⋮) in the top-right corner of the home screen.

## 📱 Settings Sections

### 🔔 Notifications
**Control notification preferences for better user experience**

#### Push Notifications
- **Purpose**: Get notified about new expenses and settlements
- **Toggle**: Enable/disable push notifications
- **Default**: Enabled
- **Use Case**: Stay updated on group activities

#### SMS Reminders  
- **Purpose**: Send SMS reminders for pending payments
- **Toggle**: Enable/disable SMS reminders
- **Default**: Disabled
- **Use Case**: Remind friends about unpaid expenses

### 🔒 Privacy & Location
**Manage location tracking and data privacy**

#### Location Tracking
- **Purpose**: Automatically attach location to expenses
- **Toggle**: Enable/disable location tracking
- **Default**: Enabled
- **Use Case**: Track where expenses were made
- **Privacy**: Location data stored locally only

### 🎨 Appearance & Preferences
**Customize app appearance and default settings**

#### Dark Mode
- **Purpose**: Use dark theme for better night viewing
- **Toggle**: Enable/disable dark mode
- **Default**: Disabled
- **Note**: Requires app restart for full effect

#### Currency
- **Purpose**: Set default currency for all expenses
- **Options**: USD ($), EUR (€), GBP (£), INR (₹), CAD (C$), AUD (A$)
- **Default**: USD ($)
- **Impact**: Affects all monetary displays

#### Default Split Type
- **Purpose**: Set default expense splitting method
- **Options**: 
  - **Equal**: Split equally among all participants
  - **Custom**: Set custom amounts for each person
  - **Percentage**: Split by percentage
- **Default**: Equal
- **Impact**: Pre-selects split method when adding expenses

### 💾 Data Management
**Export, import, or clear your data**

#### Export Data
- **Purpose**: Export all data to external file
- **Format**: JSON/CSV (coming soon)
- **Use Case**: Backup data or transfer to new device
- **Status**: Coming soon

#### Import Data
- **Purpose**: Import data from external file
- **Format**: JSON/CSV (coming soon)
- **Use Case**: Restore backup or transfer from another device
- **Status**: Coming soon

#### Clear All Data
- **Purpose**: Permanently delete all data
- **Warning**: This action cannot be undone
- **Includes**: All groups, expenses, members, and balances
- **Preserves**: App settings (currency, split type)
- **Use Case**: Start fresh or privacy cleanup

### ℹ️ About
**Learn more about the app**

#### About Expense Tracker
- **Purpose**: View app information and version details
- **Content**: App description, version, features
- **Navigation**: Opens About page

## 🔧 Technical Implementation

### Settings Storage
- **SharedPreferences**: All settings stored locally
- **Key-Value Pairs**: Simple storage for user preferences
- **Persistence**: Settings survive app restarts
- **Privacy**: No data sent to external servers

### Settings Categories
```java
// Notification Settings
notifications_enabled: boolean
sms_reminders: boolean

// Privacy Settings  
location_tracking: boolean

// Appearance Settings
dark_mode: boolean
currency: string
default_split: string
```

### Data Management
- **Export**: JSON format with all tables
- **Import**: Validate and import JSON data
- **Clear**: Delete all records from database
- **Backup**: Automatic local backup (future feature)

## 🎯 User Experience

### Settings Flow
1. **Access**: Menu → Settings
2. **Navigate**: Scroll through sections
3. **Modify**: Toggle switches or tap buttons
4. **Save**: Changes saved automatically
5. **Apply**: Some changes require app restart

### Visual Design
- **Material Design**: Modern card-based layout
- **Color Coding**: Purple for primary actions
- **Icons**: Clear visual indicators
- **Typography**: Consistent text hierarchy
- **Spacing**: Proper padding and margins

### Responsive Design
- **Scrollable**: All content accessible
- **Touch Targets**: Minimum 48dp for buttons
- **Accessibility**: Content descriptions for screen readers
- **Dark Mode**: Automatic theme switching

## 🚀 Future Enhancements

### Planned Features
- **Data Export**: JSON/CSV export functionality
- **Data Import**: Import from other expense apps
- **Cloud Sync**: Backup to cloud storage
- **Advanced Notifications**: Custom notification schedules
- **Multiple Currencies**: Per-expense currency support
- **Themes**: Multiple color themes
- **Language Support**: Multiple language options

### Advanced Settings
- **Notification Schedules**: Set quiet hours
- **Auto-Sync**: Automatic data backup
- **Security**: PIN/biometric protection
- **Analytics**: Usage statistics
- **Integration**: Calendar and contact sync

## 📋 Settings Checklist

### Initial Setup
- [ ] Enable notifications for group updates
- [ ] Set preferred currency
- [ ] Choose default split method
- [ ] Enable location tracking (optional)
- [ ] Configure dark mode preference

### Regular Maintenance
- [ ] Review notification settings
- [ ] Update currency if traveling
- [ ] Export data for backup
- [ ] Clear old data if needed
- [ ] Check for app updates

### Privacy Considerations
- [ ] Review location tracking settings
- [ ] Understand data storage
- [ ] Know what data is exported
- [ ] Clear data when needed
- [ ] Keep app updated for security

## 🎉 Benefits

### For Users
- **Personalization**: Customize app to preferences
- **Control**: Full control over data and privacy
- **Flexibility**: Multiple options for different needs
- **Transparency**: Clear understanding of settings

### For Groups
- **Consistency**: Shared settings across group
- **Efficiency**: Optimized for group workflow
- **Communication**: Better notification management
- **Data Management**: Easy backup and restore

The Settings page ensures users have complete control over their expense tracking experience! ⚙️
