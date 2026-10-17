// ==========================================
// DASHBOARD.JS - SPRING BOOT REST API BACKEND
// ==========================================

// Hosted Render Backend URL
const BACKEND_URL = 'https://company-pro-backend.onrender.com';
window.API_BASE_URL = window.API_BASE_URL || BACKEND_URL;
var API_BASE_URL = window.API_BASE_URL;

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', async function() {
    console.log('Dashboard loaded, fetching live stats from Spring Boot backend...');
    await loadDashboardData();
});

// Load real-time dashboard data from Spring Boot API
async function loadDashboardData() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/dashboard/stats`);
        if (!response.ok) {
            throw new Error(`Failed to fetch dashboard stats: HTTP ${response.status}`);
        }
        const data = await response.json();
        renderDashboardStats(data);
        renderRecentEmployees(data.recentEmployees || []);
        renderDepartmentOverview(data.departmentOverview || []);
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        showFallbackError();
    }
}

// Render real statistics cards
function renderDashboardStats(stats) {
    // Total Employees
    const totalEmployeesElem = document.getElementById('totalEmployees');
    if (totalEmployeesElem) totalEmployeesElem.textContent = stats.totalEmployees || 0;

    // Total Departments
    const totalDepartmentsElem = document.getElementById('totalDepartments');
    if (totalDepartmentsElem) totalDepartmentsElem.textContent = stats.totalDepartments || 0;

    // Present Today
    const presentTodayElem = document.getElementById('presentToday');
    if (presentTodayElem) presentTodayElem.textContent = stats.presentToday || 0;

    // On Leave
    const onLeaveElem = document.getElementById('onLeave');
    if (onLeaveElem) onLeaveElem.textContent = stats.onLeave || 0;

    // Attendance Rate
    const attendanceRateElem = document.getElementById('attendanceRate');
    if (attendanceRateElem) {
        attendanceRateElem.innerHTML = `
            <i class="fas fa-arrow-up"></i>
            <span>${stats.attendanceRate || 0}% attendance</span>
        `;
    }

    // Leave Percentage
    const leavePercentageElem = document.getElementById('leavePercentage');
    if (leavePercentageElem) {
        leavePercentageElem.innerHTML = `
            <i class="fas fa-minus"></i>
            <span>${stats.leavePercentage || 0}% on leave</span>
        `;
    }
}

// Render Recent Employees Table
function renderRecentEmployees(recentList) {
    const tbody = document.getElementById('recentEmployeesTable');
    if (!tbody) return;

    if (!recentList || recentList.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: #64748b; padding: 20px;">
                    No recent employee records found
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = recentList.map(emp => {
        const name = emp.name || `${emp.firstName || ''} ${emp.lastName || ''}`.trim();
        const code = emp.employeeCode || emp.id || '';
        const status = (emp.status || 'active').toLowerCase();

        return `
            <tr>
                <td>
                    <div class="employee-info">
                        <div class="employee-avatar">${(name.charAt(0) || 'E').toUpperCase()}</div>
                        <div>
                            <div class="employee-name">${name}</div>
                            <div class="employee-id-badge">${code}</div>
                        </div>
                    </div>
                </td>
                <td>${emp.department || 'N/A'}</td>
                <td>${emp.position || 'N/A'}</td>
                <td>
                    <span class="status-badge ${status}">
                        ${status === 'active' ? 'Active' : status === 'on-leave' ? 'On Leave' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <a href="employees.html" class="icon-btn" title="View in Employees">
                        <i class="fas fa-arrow-right"></i>
                    </a>
                </td>
            </tr>
        `;
    }).join('');
}

// Render Department Overview
function renderDepartmentOverview(deptOverview) {
    const container = document.getElementById('departmentOverview');
    if (!container) return;

    if (!deptOverview || deptOverview.length === 0) {
        container.innerHTML = '<p style="color: #64748b; text-align: center; padding: 20px;">No department distribution data available</p>';
        return;
    }

    container.innerHTML = deptOverview.map(dept => `
        <div class="dept-stat-item">
            <div class="dept-stat-icon">
                <i class="fas ${dept.icon || 'fa-building'}"></i>
            </div>
            <div class="dept-stat-info">
                <h4>${dept.name}</h4>
                <p>${dept.count} ${dept.count === 1 ? 'Employee' : 'Employees'}</p>
            </div>
            <div class="dept-stat-badge">
                ${dept.count}
            </div>
        </div>
    `).join('');
}

// Error state helper
function showFallbackError() {
    const container = document.getElementById('departmentOverview');
    if (container) {
        container.innerHTML = `
            <div style="text-align: center; padding: 20px; color: #ef4444;">
                <i class="fas fa-exclamation-circle" style="font-size: 1.5rem; margin-bottom: 8px;"></i>
                <p>Could not connect to Spring Boot API (/api/dashboard/stats).</p>
            </div>
        `;
    }
}
