// ==========================================
// EMPLOYEES.JS - SPRING BOOT REST API BACKEND
// ==========================================

// Hosted Render Backend URL
window.API_BASE_URL = window.API_BASE_URL || 'https://company-pro-backend.onrender.com';
var API_BASE_URL = window.API_BASE_URL;

// In-memory cache of current employee list
let employees = [];
let departments = [];

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('Employees page loaded, initializing with Spring Boot backend...');
    setupEventListeners();
    setupSearch();
    populateDepartmentDropdowns();
    loadEmployees();
});

// Setup All Event Listeners
function setupEventListeners() {
    // Add Employee Button
    const addBtn = document.getElementById('addEmployeeBtn');
    if (addBtn) {
        addBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openAddEmployeeModal();
        });
    }

    // Department Filter
    const departmentFilter = document.getElementById('departmentFilter');
    if (departmentFilter) {
        departmentFilter.addEventListener('change', filterEmployees);
    }
    
    // Status Filter
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', filterEmployees);
    }

    // Modal Close Buttons
    setupModalCloseButtons();

    // Add Employee Form Submit
    const addForm = document.getElementById('addEmployeeForm');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            e.preventDefault();
            addEmployee();
        });
    }

    // Edit Employee Form Submit
    const editForm = document.getElementById('editEmployeeForm');
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();
            updateEmployee();
        });
    }
}

// Populate department dropdowns from backend API
async function populateDepartmentDropdowns() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/departments`);
        if (!response.ok) throw new Error('Failed to fetch departments');
        departments = await response.json();
        const departmentNames = departments.map(d => d.name);

        // Update filter dropdown
        const filterDropdown = document.getElementById('departmentFilter');
        if (filterDropdown) {
            const currentValue = filterDropdown.value;
            filterDropdown.innerHTML = '<option value="all">All Departments</option>';
            departmentNames.forEach(dept => {
                filterDropdown.innerHTML += `<option value="${dept}">${dept}</option>`;
            });
            if (currentValue) filterDropdown.value = currentValue;
        }

        // Update add employee form dropdown
        const addFormSelect = document.querySelector('#addEmployeeForm select[name="department"]');
        if (addFormSelect) {
            const currentValue = addFormSelect.value;
            addFormSelect.innerHTML = '<option value="">Select Department</option>';
            departmentNames.forEach(dept => {
                addFormSelect.innerHTML += `<option value="${dept}">${dept}</option>`;
            });
            if (currentValue) addFormSelect.value = currentValue;
        }

        // Update edit employee form dropdown
        const editFormSelect = document.getElementById('editDepartment');
        if (editFormSelect) {
            const currentValue = editFormSelect.value;
            editFormSelect.innerHTML = '';
            departmentNames.forEach(dept => {
                editFormSelect.innerHTML += `<option value="${dept}">${dept}</option>`;
            });
            if (currentValue) editFormSelect.value = currentValue;
        }
    } catch (error) {
        console.error('Error loading departments for dropdowns:', error);
    }
}

// Load Employees from Spring Boot backend
async function loadEmployees() {
    const tbody = document.querySelector('.employee-table tbody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 40px;">
                    <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: #3b82f6; margin-bottom: 10px; display: block;"></i>
                    <p style="color: #64748b;">Loading employees from database...</p>
                </td>
            </tr>
        `;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/employees`);
        if (!response.ok) {
            throw new Error(`Server returned HTTP ${response.status}`);
        }
        employees = await response.json();
        renderEmployees(employees);
        updateEmployeeCount();
    } catch (error) {
        console.error('Failed to load employees:', error);
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" style="text-align: center; padding: 40px;">
                        <i class="fas fa-exclamation-triangle" style="font-size: 2rem; color: #ef4444; margin-bottom: 10px; display: block;"></i>
                        <p style="color: #ef4444; font-weight: 500;">Failed to load employees from backend.</p>
                        <p style="color: #64748b; font-size: 13px;">Ensure Spring Boot is running at ${API_BASE_URL || 'https://company-pro-backend.onrender.com'}</p>
                    </td>
                </tr>
            `;
        }
    }
}

// Filter Employees using backend query parameters
async function filterEmployees() {
    const departmentFilter = document.getElementById('departmentFilter');
    const statusFilter = document.getElementById('statusFilter');

    const deptVal = departmentFilter ? departmentFilter.value : 'all';
    const statusVal = statusFilter ? statusFilter.value : 'all';

    const params = new URLSearchParams();
    if (deptVal && deptVal !== 'all') params.append('department', deptVal);
    if (statusVal && statusVal !== 'all') params.append('status', statusVal);

    try {
        const url = `${API_BASE_URL}/api/employees${params.toString() ? '?' + params.toString() : ''}`;
        const response = await fetch(url);
        if (!response.ok) throw new Error('Filter query failed');
        const filteredEmployees = await response.json();
        renderEmployees(filteredEmployees);
        const countElement = document.getElementById('employeeCount');
        if (countElement) countElement.textContent = filteredEmployees.length;
    } catch (error) {
        console.error('Filter failed:', error);
    }
}

// Setup Search Functionality
function setupSearch() {
    const searchInput = document.querySelector('.search-box input');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', function(e) {
            clearTimeout(debounceTimer);
            const searchTerm = e.target.value.trim();
            debounceTimer = setTimeout(() => {
                searchEmployees(searchTerm);
            }, 300);
        });
    }
}

// Search Employees against backend search API
async function searchEmployees(searchTerm) {
    if (!searchTerm) {
        loadEmployees();
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/employees/search?query=${encodeURIComponent(searchTerm)}`);
        if (!response.ok) throw new Error('Search failed');
        const results = await response.json();
        renderEmployees(results);
        const countElement = document.getElementById('employeeCount');
        if (countElement) countElement.textContent = results.length;
    } catch (error) {
        console.error('Search failed:', error);
    }
}

// Render Employees Table
function renderEmployees(employeeList) {
    const tbody = document.querySelector('.employee-table tbody');
    if (!tbody) return;

    if (!employeeList || employeeList.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 40px;">
                    <i class="fas fa-users" style="font-size: 3rem; color: #cbd5e1; margin-bottom: 10px; display: block;"></i>
                    <p style="color: #64748b;">No employees found</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = employeeList.map(employee => {
        const empCode = employee.employeeCode || employee.id || 'N/A';
        const empName = employee.name || `${employee.firstName || ''} ${employee.lastName || ''}`.trim();
        const empStatus = (employee.status || 'active').toLowerCase();
        const salaryVal = employee.salary ? parseFloat(employee.salary).toLocaleString('en-IN') : '0';

        return `
            <tr>
                <td>
                    <div class="employee-info">
                        <div class="employee-avatar">${(empName.charAt(0) || 'E').toUpperCase()}</div>
                        <div>
                            <div class="employee-name">${empName}</div>
                            <div class="employee-id-badge">${empCode}</div>
                        </div>
                    </div>
                </td>
                <td>${employee.email || ''}</td>
                <td>${employee.phone || ''}</td>
                <td>${employee.department || ''}</td>
                <td>${employee.position || ''}</td>
                <td>₹${salaryVal}</td>
                <td>${formatDate(employee.joiningDate)}</td>
                <td>
                    <span class="status-badge ${empStatus}">
                        ${empStatus === 'active' ? 'Active' : empStatus === 'on-leave' ? 'On Leave' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <div class="action-buttons">
                        <button class="icon-btn" onclick="viewEmployeeDetails('${empCode}')" title="View Details">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="icon-btn" onclick="editEmployeeDetails('${empCode}')" title="Edit">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="icon-btn danger" onclick="deleteEmployeeConfirm('${empCode}')" title="Delete">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// Update Employee Count
function updateEmployeeCount() {
    const countElement = document.getElementById('employeeCount');
    if (countElement) {
        countElement.textContent = employees.length;
    }
}

// Open Add Employee Modal
function openAddEmployeeModal() {
    const modal = document.getElementById('addEmployeeModal');
    if (modal) {
        const form = document.getElementById('addEmployeeForm');
        if (form) form.reset();
        populateDepartmentDropdowns();
        modal.style.display = 'block';
    }
}

// Add Employee - SUBMIT TO SPRING BOOT POST /api/employees
async function addEmployee() {
    const form = document.getElementById('addEmployeeForm');
    if (!form) return;

    const formData = new FormData(form);
    const payload = {
        name: formData.get('name') ? formData.get('name').trim() : '',
        email: formData.get('email') ? formData.get('email').trim() : '',
        phone: formData.get('phone') ? formData.get('phone').trim() : '',
        department: formData.get('department'),
        position: formData.get('position') ? formData.get('position').trim() : '',
        salary: parseFloat(formData.get('salary')),
        joiningDate: formData.get('joiningDate'),
        status: 'active',
        address: formData.get('address') || '',
        emergencyContact: formData.get('emergencyContact') || '',
        bloodGroup: formData.get('bloodGroup') || '',
        education: formData.get('education') || ''
    };

    // Client validation
    if (!payload.name || !payload.email || !payload.department || !payload.position || isNaN(payload.salary) || !payload.joiningDate) {
        alert('Please fill all required fields correctly.');
        return;
    }

    if (payload.salary <= 0) {
        alert('Salary must be a positive number.');
        return;
    }

    const submitBtn = form.querySelector('button[type="submit"]') || document.querySelector('button[form="addEmployeeForm"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/employees`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok && response.status === 201) {
            document.getElementById('addEmployeeModal').style.display = 'none';
            form.reset();
            alert(`✅ Employee created successfully!\nAssigned ID: ${data.employeeCode || data.id}`);
            await loadEmployees();
        } else {
            let errorMsg = data.message || 'Failed to save employee.';
            if (data.errors) {
                const fieldErrors = Object.entries(data.errors).map(([k, v]) => `• ${k}: ${v}`).join('\n');
                errorMsg += `\n\n${fieldErrors}`;
            }
            alert(`❌ Error:\n${errorMsg}`);
        }
    } catch (error) {
        console.error('Add employee error:', error);
        alert('❌ Could not connect to Spring Boot backend. Please check your connection.');
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-save"></i> Add Employee';
        }
    }
}

// View Employee Details
async function viewEmployeeDetails(id) {
    let employee = employees.find(emp => (emp.employeeCode === id || emp.id === id));

    if (!employee) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/employees/${id}`);
            if (res.ok) employee = await res.json();
        } catch (e) {
            console.error('Fetch employee detail failed:', e);
        }
    }

    if (!employee) {
        alert('Employee details could not be found.');
        return;
    }

    const modal = document.getElementById('viewEmployeeModal');
    if (!modal) return;

    const empName = employee.name || `${employee.firstName || ''} ${employee.lastName || ''}`.trim();
    const empCode = employee.employeeCode || employee.id || '';
    const empStatus = (employee.status || 'active').toLowerCase();

    document.getElementById('viewEmployeeId').textContent = empCode;
    document.getElementById('viewEmployeeName').textContent = empName;
    document.getElementById('viewEmployeeEmail').textContent = employee.email || 'N/A';
    document.getElementById('viewEmployeePhone').textContent = employee.phone || 'N/A';
    document.getElementById('viewEmployeeDepartment').textContent = employee.department || 'N/A';
    document.getElementById('viewEmployeePosition').textContent = employee.position || 'N/A';
    document.getElementById('viewEmployeeSalary').textContent = '₹' + (employee.salary ? parseFloat(employee.salary).toLocaleString('en-IN') : '0');
    document.getElementById('viewEmployeeJoining').textContent = formatDate(employee.joiningDate);
    document.getElementById('viewEmployeeStatus').innerHTML = `<span class="status-badge ${empStatus}">${empStatus === 'active' ? 'Active' : empStatus === 'on-leave' ? 'On Leave' : 'Inactive'}</span>`;
    document.getElementById('viewEmployeeAddress').textContent = employee.address || 'N/A';
    document.getElementById('viewEmployeeEmergency').textContent = employee.emergencyContact || 'N/A';
    document.getElementById('viewEmployeeBlood').textContent = employee.bloodGroup || 'N/A';
    document.getElementById('viewEmployeeEducation').textContent = employee.education || 'N/A';

    modal.style.display = 'block';
}

// Edit Employee Details
async function editEmployeeDetails(id) {
    let employee = employees.find(emp => (emp.employeeCode === id || emp.id === id));

    if (!employee) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/employees/${id}`);
            if (res.ok) employee = await res.json();
        } catch (e) {
            console.error('Fetch employee for edit failed:', e);
        }
    }

    if (!employee) {
        alert('Employee details could not be found.');
        return;
    }

    const modal = document.getElementById('editEmployeeModal');
    if (!modal) return;

    await populateDepartmentDropdowns();

    const empCode = employee.employeeCode || employee.id || '';
    document.getElementById('editEmployeeId').value = empCode;
    document.getElementById('editName').value = employee.name || '';
    document.getElementById('editEmail').value = employee.email || '';
    document.getElementById('editPhone').value = employee.phone || '';
    document.getElementById('editDepartment').value = employee.department || '';
    document.getElementById('editPosition').value = employee.position || '';
    document.getElementById('editSalary').value = employee.salary || '';
    document.getElementById('editJoiningDate').value = employee.joiningDate || '';
    document.getElementById('editStatus').value = (employee.status || 'active').toLowerCase();
    document.getElementById('editAddress').value = employee.address || '';
    document.getElementById('editEmergencyContact').value = employee.emergencyContact || '';
    document.getElementById('editBloodGroup').value = employee.bloodGroup || '';
    document.getElementById('editEducation').value = employee.education || '';

    modal.style.display = 'block';
}

// Update Employee - SUBMIT TO SPRING BOOT PUT /api/employees/{id}
async function updateEmployee() {
    const form = document.getElementById('editEmployeeForm');
    if (!form) return;

    const id = document.getElementById('editEmployeeId').value;
    if (!id) {
        alert('Employee identifier is missing.');
        return;
    }

    const formData = new FormData(form);
    const payload = {
        employeeCode: id,
        name: formData.get('name') ? formData.get('name').trim() : '',
        email: formData.get('email') ? formData.get('email').trim() : '',
        phone: formData.get('phone') ? formData.get('phone').trim() : '',
        department: formData.get('department'),
        position: formData.get('position') ? formData.get('position').trim() : '',
        salary: parseFloat(formData.get('salary')),
        joiningDate: formData.get('joiningDate'),
        status: formData.get('status'),
        address: formData.get('address') || '',
        emergencyContact: formData.get('emergencyContact') || '',
        bloodGroup: formData.get('bloodGroup') || '',
        education: formData.get('education') || ''
    };

    if (!payload.name || !payload.email || !payload.department || !payload.position || isNaN(payload.salary) || !payload.joiningDate) {
        alert('Please fill all required fields correctly.');
        return;
    }

    const submitBtn = form.querySelector('button[type="submit"]') || document.querySelector('button[form="editEmployeeForm"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/employees/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            document.getElementById('editEmployeeModal').style.display = 'none';
            alert('✅ Employee updated successfully in database!');
            await loadEmployees();
        } else {
            let errorMsg = data.message || 'Failed to update employee.';
            if (data.errors) {
                const fieldErrors = Object.entries(data.errors).map(([k, v]) => `• ${k}: ${v}`).join('\n');
                errorMsg += `\n\n${fieldErrors}`;
            }
            alert(`❌ Error:\n${errorMsg}`);
        }
    } catch (error) {
        console.error('Update employee error:', error);
        alert('❌ Could not connect to Spring Boot backend.');
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-save"></i> Update Employee';
        }
    }
}

// Delete Employee - SUBMIT TO SPRING BOOT DELETE /api/employees/{id}
async function deleteEmployeeConfirm(id) {
    if (!confirm(`Are you sure you want to delete employee ${id}? This action will permanently remove the record from H2 database.`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/employees/${id}`, {
            method: 'DELETE'
        });

        if (response.ok || response.status === 204) {
            alert('✅ Employee deleted successfully from database!');
            await loadEmployees();
        } else {
            const data = await response.json().catch(() => ({}));
            alert(`❌ Delete failed: ${data.message || 'Unable to delete employee.'}`);
        }
    } catch (error) {
        console.error('Delete employee error:', error);
        alert('❌ Could not connect to Spring Boot backend.');
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

// Format Date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-IN', options);
}

// Export functions to global scope
window.openAddEmployeeModal = openAddEmployeeModal;
window.viewEmployeeDetails = viewEmployeeDetails;
window.editEmployeeDetails = editEmployeeDetails;
window.deleteEmployeeConfirm = deleteEmployeeConfirm;
