// ==========================================
// REPORTS.JS - WITH WORD FORMAT DOWNLOAD
// ==========================================

document.addEventListener('DOMContentLoaded', function() {
    setupReportButtons();
});

function setupReportButtons() {
    // Employee Report
    const empReportBtn = document.querySelector('[onclick="generateEmployeeReport()"]');
    if (empReportBtn) {
        empReportBtn.onclick = generateEmployeeReport;
    }

    // Department Report
    const deptReportBtn = document.querySelector('[onclick="generateDepartmentReport()"]');
    if (deptReportBtn) {
        deptReportBtn.onclick = generateDepartmentReport;
    }

    // Salary Report
    const salaryReportBtn = document.querySelector('[onclick="generateSalaryReport()"]');
    if (salaryReportBtn) {
        salaryReportBtn.onclick = generateSalaryReport;
    }

    // Attendance Report
    const attendanceReportBtn = document.querySelector('[onclick="generateAttendanceReport()"]');
    if (attendanceReportBtn) {
        attendanceReportBtn.onclick = generateAttendanceReport;
    }
}

// Generate Employee Report in WORD FORMAT
function generateEmployeeReport() {
    // Get employee data
    const employees = getEmployeeData();
    
    // Create Word document content
    let docContent = `
        <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
        <head>
            <meta charset='utf-8'>
            <title>Employee Report</title>
            <style>
                body { font-family: Arial, sans-serif; }
                h1 { color: #3256B1; text-align: center; }
                h2 { color: #3256B1; margin-top: 30px; }
                table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #3256B1; color: white; font-weight: bold; }
                tr:nth-child(even) { background-color: #f9fafb; }
                .header { margin-bottom: 30px; }
                .date { color: #64748b; margin-bottom: 20px; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>CompanyPro - Employee Report</h1>
                <p class="date">Generated on: ${new Date().toLocaleDateString('en-IN', { 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })}</p>
            </div>
            
            <h2>Employee Summary</h2>
            <p>Total Employees: <strong>${employees.length}</strong></p>
            <p>Active Employees: <strong>${employees.filter(e => e.status === 'active').length}</strong></p>
            <p>On Leave: <strong>${employees.filter(e => e.status === 'on-leave').length}</strong></p>
            
            <h2>Complete Employee List</h2>
            <table>
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Department</th>
                        <th>Position</th>
                        <th>Salary</th>
                        <th>Joining Date</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    ${employees.map(emp => `
                        <tr>
                            <td>${emp.id}</td>
                            <td>${emp.name}</td>
                            <td>${emp.email}</td>
                            <td>${emp.phone}</td>
                            <td>${emp.department}</td>
                            <td>${emp.position}</td>
                            <td>₹${emp.salary.toLocaleString('en-IN')}</td>
                            <td>${emp.joiningDate}</td>
                            <td>${emp.status}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </body>
        </html>
    `;
    
    // Download as Word document
    downloadWordDocument(docContent, 'Employee_Report.doc');
}

// Generate Department Report in WORD FORMAT
function generateDepartmentReport() {
    const departments = getDepartmentData();
    
    let docContent = `
        <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
        <head>
            <meta charset='utf-8'>
            <title>Department Report</title>
            <style>
                body { font-family: Arial, sans-serif; }
                h1 { color: #3256B1; text-align: center; }
                h2 { color: #3256B1; margin-top: 30px; }
                table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #3256B1; color: white; font-weight: bold; }
                tr:nth-child(even) { background-color: #f9fafb; }
                .header { margin-bottom: 30px; }
                .date { color: #64748b; margin-bottom: 20px; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>CompanyPro - Department Report</h1>
                <p class="date">Generated on: ${new Date().toLocaleDateString('en-IN', { 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })}</p>
            </div>
            
            <h2>Department Summary</h2>
            <p>Total Departments: <strong>${departments.length}</strong></p>
            <p>Total Budget: <strong>₹${departments.reduce((sum, d) => sum + d.budget, 0).toLocaleString('en-IN')}</strong></p>
            
            <h2>Complete Department List</h2>
            <table>
                <thead>
                    <tr>
                        <th>Department ID</th>
                        <th>Name</th>
                        <th>Manager</th>
                        <th>Employees</th>
                        <th>Budget</th>
                        <th>Location</th>
                    </tr>
                </thead>
                <tbody>
                    ${departments.map(dept => `
                        <tr>
                            <td>${dept.id}</td>
                            <td>${dept.name}</td>
                            <td>${dept.manager}</td>
                            <td>${dept.employeeCount}</td>
                            <td>₹${dept.budget.toLocaleString('en-IN')}</td>
                            <td>${dept.location}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </body>
        </html>
    `;
    
    downloadWordDocument(docContent, 'Department_Report.doc');
}

// Generate Salary Report
function generateSalaryReport() {
    const employees = getEmployeeData();
    const totalSalary = employees.reduce((sum, emp) => sum + emp.salary, 0);
    const avgSalary = totalSalary / employees.length;
    
    let docContent = `
        <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
        <head>
            <meta charset='utf-8'>
            <title>Salary Report</title>
            <style>
                body { font-family: Arial, sans-serif; }
                h1 { color: #3256B1; text-align: center; }
                h2 { color: #3256B1; margin-top: 30px; }
                table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #3256B1; color: white; font-weight: bold; }
                tr:nth-child(even) { background-color: #f9fafb; }
                .header { margin-bottom: 30px; }
                .date { color: #64748b; margin-bottom: 20px; }
                .summary-box { background: #f8fafc; padding: 20px; margin: 20px 0; border-left: 4px solid #3256B1; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>CompanyPro - Salary Report</h1>
                <p class="date">Generated on: ${new Date().toLocaleDateString('en-IN', { 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                })}</p>
            </div>
            
            <div class="summary-box">
                <h2>Salary Summary</h2>
                <p>Total Monthly Payroll: <strong>₹${totalSalary.toLocaleString('en-IN')}</strong></p>
                <p>Average Salary: <strong>₹${Math.round(avgSalary).toLocaleString('en-IN')}</strong></p>
                <p>Highest Salary: <strong>₹${Math.max(...employees.map(e => e.salary)).toLocaleString('en-IN')}</strong></p>
                <p>Lowest Salary: <strong>₹${Math.min(...employees.map(e => e.salary)).toLocaleString('en-IN')}</strong></p>
            </div>
            
            <h2>Employee Salary Details</h2>
            <table>
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Position</th>
                        <th>Monthly Salary</th>
                        <th>Annual Salary</th>
                    </tr>
                </thead>
                <tbody>
                    ${employees.map(emp => `
                        <tr>
                            <td>${emp.id}</td>
                            <td>${emp.name}</td>
                            <td>${emp.department}</td>
                            <td>${emp.position}</td>
                            <td>₹${emp.salary.toLocaleString('en-IN')}</td>
                            <td>₹${(emp.salary * 12).toLocaleString('en-IN')}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </body>
        </html>
    `;
    
    downloadWordDocument(docContent, 'Salary_Report.doc');
}

// Generate Attendance Report
function generateAttendanceReport() {
    const employees = getEmployeeData();
    
    let docContent = `
        <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
        <head>
            <meta charset='utf-8'>
            <title>Attendance Report</title>
            <style>
                body { font-family: Arial, sans-serif; }
                h1 { color: #3256B1; text-align: center; }
                h2 { color: #3256B1; margin-top: 30px; }
                table { border-collapse: collapse; width: 100%; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #3256B1; color: white; font-weight: bold; }
                tr:nth-child(even) { background-color: #f9fafb; }
                .header { margin-bottom: 30px; }
                .date { color: #64748b; margin-bottom: 20px; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>CompanyPro - Attendance Report</h1>
                <p class="date">Month: ${new Date().toLocaleDateString('en-IN', { 
                    year: 'numeric', 
                    month: 'long'
                })}</p>
            </div>
            
            <h2>Attendance Summary</h2>
            <table>
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Status</th>
                        <th>Present Days</th>
                        <th>Absent Days</th>
                        <th>Leave Days</th>
                    </tr>
                </thead>
                <tbody>
                    ${employees.map(emp => `
                        <tr>
                            <td>${emp.id}</td>
                            <td>${emp.name}</td>
                            <td>${emp.department}</td>
                            <td>${emp.status}</td>
                            <td>${Math.floor(Math.random() * 5) + 18}</td>
                            <td>${Math.floor(Math.random() * 3)}</td>
                            <td>${emp.status === 'on-leave' ? Math.floor(Math.random() * 5) + 1 : 0}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </body>
        </html>
    `;
    
    downloadWordDocument(docContent, 'Attendance_Report.doc');
}

// Download Word Document Function
function downloadWordDocument(content, filename) {
    const blob = new Blob(['\ufeff', content], {
        type: 'application/msword'
    });
    
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    
    alert(`${filename} downloaded successfully!`);
}

// Get Employee Data
function getEmployeeData() {
    // This would normally come from your database or API
    // For now, using sample data
    return [
        {
            id: 'EMP001',
            name: 'John Doe',
            email: 'john.doe@company.com',
            phone: '+91 9876543210',
            department: 'Engineering',
            position: 'Senior Developer',
            salary: 85000,
            joiningDate: '2022-01-15',
            status: 'active'
        },
        {
            id: 'EMP002',
            name: 'Jane Smith',
            email: 'jane.smith@company.com',
            phone: '+91 9876543220',
            department: 'Marketing',
            position: 'Marketing Manager',
            salary: 75000,
            joiningDate: '2022-03-20',
            status: 'active'
        },
        {
            id: 'EMP003',
            name: 'Mike Johnson',
            email: 'mike.johnson@company.com',
            phone: '+91 9876543230',
            department: 'Sales',
            position: 'Sales Executive',
            salary: 65000,
            joiningDate: '2022-05-10',
            status: 'active'
        },
        {
            id: 'EMP004',
            name: 'Sarah Williams',
            email: 'sarah.williams@company.com',
            phone: '+91 9876543240',
            department: 'HR',
            position: 'HR Specialist',
            salary: 60000,
            joiningDate: '2022-07-01',
            status: 'active'
        },
        {
            id: 'EMP005',
            name: 'Robert Brown',
            email: 'robert.brown@company.com',
            phone: '+91 9876543250',
            department: 'Finance',
            position: 'Accountant',
            salary: 70000,
            joiningDate: '2021-12-15',
            status: 'on-leave'
        }
    ];
}

// Get Department Data
function getDepartmentData() {
    return [
        {
            id: 'DEPT001',
            name: 'Engineering',
            manager: 'John Doe',
            employeeCount: 45,
            budget: 5000000,
            location: 'Building A, Floor 3'
        },
        {
            id: 'DEPT002',
            name: 'Marketing',
            manager: 'Jane Smith',
            employeeCount: 28,
            budget: 3000000,
            location: 'Building B, Floor 2'
        },
        {
            id: 'DEPT003',
            name: 'Sales',
            manager: 'Mike Johnson',
            employeeCount: 35,
            budget: 2500000,
            location: 'Building A, Floor 1'
        },
        {
            id: 'DEPT004',
            name: 'HR',
            manager: 'Sarah Williams',
            employeeCount: 15,
            budget: 1500000,
            location: 'Building B, Floor 1'
        },
        {
            id: 'DEPT005',
            name: 'Finance',
            manager: 'Robert Brown',
            employeeCount: 20,
            budget: 2000000,
            location: 'Building A, Floor 2'
        }
    ];
}

// Export functions
window.generateEmployeeReport = generateEmployeeReport;
window.generateDepartmentReport = generateDepartmentReport;
window.generateSalaryReport = generateSalaryReport;
window.generateAttendanceReport = generateAttendanceReport;
