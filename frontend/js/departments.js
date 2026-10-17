// ==========================================
// DEPARTMENTS.JS - SPRING BOOT REST API BACKEND
// ==========================================

// Hosted Render Backend URL
const BACKEND_URL = 'https://company-pro-backend.onrender.com';
window.API_BASE_URL = window.API_BASE_URL || BACKEND_URL;
var API_BASE_URL = window.API_BASE_URL;

let departments = [];

// Initialize
document.addEventListener('DOMContentLoaded', async function() {
    console.log('Departments page loaded, connecting to Spring Boot...');
    await loadDepartments();
    setupEventListeners();
});

// Setup Event Listeners
function setupEventListeners() {
    const addBtn = document.getElementById('addDepartmentBtn');
    if (addBtn) {
        addBtn.addEventListener('click', openAddDepartmentModal);
    }

    setupModalCloseButtons();

    const addForm = document.getElementById('addDepartmentForm');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            e.preventDefault();
            addDepartment();
        });
    }

    const editForm = document.getElementById('editDepartmentForm');
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();
            updateDepartment();
        });
    }

    const searchInput = document.querySelector('.search-box input');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const query = e.target.value.toLowerCase().trim();
            filterDepartmentsLocal(query);
        });
    }
}

// Setup Modal Close Buttons
function setupModalCloseButtons() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        const closeBtn = modal.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                modal.style.display = 'none';
            });
        }
    });

    window.addEventListener('click', function(e) {
        modals.forEach(modal => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
}

// Load Departments from Spring Boot
async function loadDepartments() {
    const grid = document.querySelector('.departments-grid');
    if (grid) {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: #3b82f6; margin-bottom: 10px; display: block;"></i>
                <p style="color: #64748b;">Loading departments from database...</p>
            </div>
        `;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/departments`);
        if (!response.ok) throw new Error(`HTTP error ${response.status}`);
        departments = await response.json();
        renderDepartments(departments);
    } catch (error) {
        console.error('Failed to load departments:', error);
        if (grid) {
            grid.innerHTML = `
                <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 2rem; color: #ef4444; margin-bottom: 10px; display: block;"></i>
                    <p style="color: #ef4444; font-weight: 500;">Failed to load departments from backend.</p>
                </div>
            `;
        }
    }
}

// Render Departments Grid
function renderDepartments(deptList) {
    const grid = document.querySelector('.departments-grid');
    if (!grid) return;

    if (!deptList || deptList.length === 0) {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <i class="fas fa-building" style="font-size: 3rem; color: #cbd5e1; margin-bottom: 10px; display: block;"></i>
                <p style="color: #64748b;">No departments found</p>
            </div>
        `;
        return;
    }

    const icons = {
        'Engineering': 'fa-code',
        'Marketing': 'fa-bullhorn',
        'Sales': 'fa-chart-line',
        'HR': 'fa-users',
        'Finance': 'fa-dollar-sign'
    };

    grid.innerHTML = deptList.map(dept => {
        const code = dept.departmentCode || dept.id;
        const icon = icons[dept.name] || 'fa-building';
        const empCount = dept.employeeCount || 0;
        const totalSalary = dept.totalSalary ? parseFloat(dept.totalSalary).toLocaleString('en-IN') : '0';
        const budget = dept.budget ? parseFloat(dept.budget).toLocaleString('en-IN') : '0';

        return `
            <div class="department-card">
                <div class="department-header">
                    <div class="dept-icon-large">
                        <i class="fas ${icon}"></i>
                    </div>
                    <div>
                        <h3>${dept.name}</h3>
                        <span class="dept-id-badge">${code}</span>
                    </div>
                </div>

                <p class="dept-description">${dept.description || 'No description provided.'}</p>

                <div class="dept-info-grid">
                    <div class="dept-info-item">
                        <label>Manager</label>
                        <p>${dept.manager || 'N/A'}</p>
                    </div>
                    <div class="dept-info-item">
                        <label>Location</label>
                        <p>${dept.location || 'N/A'}</p>
                    </div>
                    <div class="dept-info-item">
                        <label>Employees</label>
                        <p><strong>${empCount}</strong></p>
                    </div>
                    <div class="dept-info-item">
                        <label>Budget</label>
                        <p>₹${budget}</p>
                    </div>
                </div>

                <div class="dept-actions">
                    <button class="btn-action view" onclick="viewDepartmentDetails('${code}')">
                        <i class="fas fa-eye"></i> View
                    </button>
                    <button class="btn-action edit" onclick="editDepartmentDetails('${code}')">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="btn-action delete" onclick="deleteDepartmentConfirm('${code}')">
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

// Filter Departments locally
function filterDepartmentsLocal(query) {
    if (!query) {
        renderDepartments(departments);
        return;
    }
    const filtered = departments.filter(d =>
        d.name.toLowerCase().includes(query) ||
        (d.departmentCode && d.departmentCode.toLowerCase().includes(query)) ||
        (d.manager && d.manager.toLowerCase().includes(query)) ||
        (d.location && d.location.toLowerCase().includes(query))
    );
    renderDepartments(filtered);
}

// Open Add Department Modal
function openAddDepartmentModal() {
    const modal = document.getElementById('addDepartmentModal');
    if (modal) {
        const form = document.getElementById('addDepartmentForm');
        if (form) form.reset();
        modal.style.display = 'block';
    }
}

// Add Department - POST /api/departments
async function addDepartment() {
    const form = document.getElementById('addDepartmentForm');
    if (!form) return;

    const formData = new FormData(form);
    const payload = {
        name: formData.get('name').trim(),
        manager: formData.get('manager').trim(),
        managerEmail: formData.get('managerEmail').trim(),
        managerPhone: formData.get('managerPhone').trim(),
        location: formData.get('location').trim(),
        budget: parseFloat(formData.get('budget')),
        description: formData.get('description') ? formData.get('description').trim() : ''
    };

    if (!payload.name || !payload.manager || !payload.managerEmail || !payload.location || isNaN(payload.budget)) {
        alert('Please fill all required fields correctly.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/departments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok && response.status === 201) {
            document.getElementById('addDepartmentModal').style.display = 'none';
            form.reset();
            alert(`✅ Department created successfully! (Code: ${data.departmentCode || data.id})`);
            await loadDepartments();
        } else {
            alert(`❌ Error: ${data.message || 'Failed to create department.'}`);
        }
    } catch (error) {
        console.error('Add department error:', error);
        alert('❌ Could not connect to Spring Boot backend.');
    }
}

// View Department Details
async function viewDepartmentDetails(id) {
    let dept = departments.find(d => (d.departmentCode === id || d.id === id));

    if (!dept) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/departments/${id}`);
            if (res.ok) dept = await res.json();
        } catch (e) {
            console.error('Fetch department details failed:', e);
        }
    }

    if (!dept) {
        alert('Department details could not be found.');
        return;
    }

    const modal = document.getElementById('viewDepartmentModal');
    if (!modal) return;

    const code = dept.departmentCode || dept.id;
    document.getElementById('viewDeptId').textContent = code;
    document.getElementById('viewDeptName').textContent = dept.name;
    document.getElementById('viewDeptManager').textContent = dept.manager || 'N/A';
    document.getElementById('viewDeptManagerEmail').textContent = dept.managerEmail || 'N/A';
    document.getElementById('viewDeptManagerPhone').textContent = dept.managerPhone || 'N/A';
    document.getElementById('viewDeptLocation').textContent = dept.location || 'N/A';
    document.getElementById('viewDeptBudget').textContent = '₹' + (dept.budget ? parseFloat(dept.budget).toLocaleString('en-IN') : '0');
    document.getElementById('viewDeptDescription').textContent = dept.description || 'N/A';

    // Fetch employees in this department
    const employeesContainer = document.getElementById('viewDeptEmployees');
    if (employeesContainer) {
        employeesContainer.innerHTML = '<p style="color: #64748b;"><i class="fas fa-spinner fa-spin"></i> Loading employees...</p>';
        try {
            const empRes = await fetch(`${API_BASE_URL}/api/employees?department=${encodeURIComponent(dept.name)}`);
            if (empRes.ok) {
                const deptEmps = await empRes.json();
                document.getElementById('viewDeptEmployeeCount').textContent = deptEmps.length;
                if (deptEmps.length === 0) {
                    employeesContainer.innerHTML = '<p style="color: #64748b;">No employees currently assigned to this department.</p>';
                } else {
                    employeesContainer.innerHTML = `
                        <table class="employee-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Position</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${deptEmps.map(e => `
                                    <tr>
                                        <td>${e.employeeCode || e.id}</td>
                                        <td>${e.name}</td>
                                        <td>${e.position}</td>
                                        <td><span class="status-badge ${e.status}">${e.status}</span></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    `;
                }
            }
        } catch (err) {
            employeesContainer.innerHTML = '<p style="color: #ef4444;">Failed to load employees.</p>';
        }
    }

    modal.style.display = 'block';
}

// Edit Department Details
async function editDepartmentDetails(id) {
    let dept = departments.find(d => (d.departmentCode === id || d.id === id));

    if (!dept) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/departments/${id}`);
            if (res.ok) dept = await res.json();
        } catch (e) {
            console.error('Fetch department for edit failed:', e);
        }
    }

    if (!dept) {
        alert('Department details could not be found.');
        return;
    }

    const modal = document.getElementById('editDepartmentModal');
    if (!modal) return;

    const code = dept.departmentCode || dept.id;
    document.getElementById('editDepartmentId').value = code;
    document.getElementById('editDeptName').value = dept.name || '';
    document.getElementById('editManager').value = dept.manager || '';
    document.getElementById('editManagerEmail').value = dept.managerEmail || '';
    document.getElementById('editManagerPhone').value = dept.managerPhone || '';
    document.getElementById('editLocation').value = dept.location || '';
    document.getElementById('editBudget').value = dept.budget || '';
    document.getElementById('editDescription').value = dept.description || '';

    modal.style.display = 'block';
}

// Update Department - PUT /api/departments/{id}
async function updateDepartment() {
    const form = document.getElementById('editDepartmentForm');
    if (!form) return;

    const id = document.getElementById('editDepartmentId').value;
    if (!id) {
        alert('Department identifier is missing.');
        return;
    }

    const formData = new FormData(form);
    const payload = {
        departmentCode: id,
        name: formData.get('name').trim(),
        manager: formData.get('manager').trim(),
        managerEmail: formData.get('managerEmail').trim(),
        managerPhone: formData.get('managerPhone').trim(),
        location: formData.get('location').trim(),
        budget: parseFloat(formData.get('budget')),
        description: formData.get('description') ? formData.get('description').trim() : ''
    };

    if (!payload.name || !payload.manager || !payload.managerEmail || !payload.location || isNaN(payload.budget)) {
        alert('Please fill all required fields correctly.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/departments/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            document.getElementById('editDepartmentModal').style.display = 'none';
            alert('✅ Department updated successfully!');
            await loadDepartments();
        } else {
            alert(`❌ Error: ${data.message || 'Failed to update department.'}`);
        }
    } catch (error) {
        console.error('Update department error:', error);
        alert('❌ Could not connect to Spring Boot backend.');
    }
}

// Delete Department - DELETE /api/departments/{id}
async function deleteDepartmentConfirm(id) {
    if (!confirm(`Are you sure you want to delete department ${id}? This action will permanently remove it from the database.`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/departments/${id}`, {
            method: 'DELETE'
        });

        if (response.ok || response.status === 204) {
            alert('✅ Department deleted successfully from database!');
            await loadDepartments();
        } else {
            const data = await response.json().catch(() => ({}));
            alert(`❌ Delete failed: ${data.message || 'Unable to delete department.'}`);
        }
    } catch (error) {
        console.error('Delete department error:', error);
        alert('❌ Could not connect to Spring Boot backend.');
    }
}

window.viewDepartmentDetails = viewDepartmentDetails;
window.editDepartmentDetails = editDepartmentDetails;
window.deleteDepartmentConfirm = deleteDepartmentConfirm;
