# Implementation Plan - Enhancing Dashboard with Forms and Notice Board

This plan covers adding functional forms for adding students and company drives, and integrating a Notice Board into the Dashboard.

## Proposed Changes

### Dashboard Enhancements

#### [MODIFY] [Dashboard.java](file:///D:/placement management system/placement management system/src/Dashboard.java)
- Update layout to include a side Notice Board next to the table.
- Implement `showAddStudentForm()` to capture student details and update the table.
- Implement `showAddCompanyForm()` to capture company drive details and update the table.
- Update action listeners for "Add Student" and "Add Company Drive" buttons.

## Verification Plan

### Manual Verification
1. Run the application and login.
2. Verify the new **Notice Board** section is visible next to the table.
3. Click **+ Add Student**, fill the form, and verify the new row appears in the table (if applicable/designed to show there) or a success message.
4. Click **+ Add Company Drive**, fill the form, and verify the new drive appears in the table.
