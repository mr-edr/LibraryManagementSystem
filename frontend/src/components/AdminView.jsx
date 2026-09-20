import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { ShieldCheck, User as UserIcon, RefreshCw, CheckCircle, XCircle } from 'lucide-react';

export default function AdminView({ onNotify }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await api.getAllUsers();
      setUsers(data || []);
    } catch (err) {
      onNotify(err.message || 'Failed to load user registry', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      setUpdatingId(userId);
      await api.updateUserRole(userId, newRole);
      onNotify(`User role updated to ${newRole}`, 'success');
      fetchUsers();
    } catch (err) {
      onNotify(err.message || 'Failed to update user role', 'error');
    } finally {
      setUpdatingId(null);
    }
  };

  const handleStatusToggle = async (userId, currentStatus) => {
    try {
      setUpdatingId(userId);
      await api.toggleUserStatus(userId, !currentStatus);
      onNotify(`Account status updated`, 'success');
      fetchUsers();
    } catch (err) {
      onNotify(err.message || 'Failed to toggle account status', 'error');
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div>
      <div className="section-header">
        <div>
          <h1 className="section-title">Admin Console</h1>
          <p className="section-subtitle">
            Manage user accounts, assign Librarian permissions, and configure access controls
          </p>
        </div>

        <button className="btn btn-outline" onClick={fetchUsers} disabled={loading}>
          <RefreshCw size={15} className={loading ? 'animate-spin' : ''} />
          <span>Refresh</span>
        </button>
      </div>

      {loading ? (
        <div className="empty-state">
          <p>Loading user directory...</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>User ID</th>
                <th>Full Name</th>
                <th>Username</th>
                <th>Assigned Role</th>
                <th>Account Status</th>
                <th style={{ textAlign: 'right' }}>Security Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.userId}>
                  <td>#{u.userId}</td>
                  <td>
                    <strong>{u.name}</strong>
                  </td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      <UserIcon size={14} className="text-slate-400" />
                      <span>@{u.userName}</span>
                    </div>
                  </td>
                  <td>
                    <select
                      className="filter-select"
                      style={{ padding: '0.3rem 0.6rem', fontSize: '0.85rem' }}
                      value={u.role}
                      disabled={updatingId === u.userId}
                      onChange={(e) => handleRoleChange(u.userId, e.target.value)}
                    >
                      <option value="USER">USER</option>
                      <option value="LIBRARIAN">LIBRARIAN</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </td>
                  <td>
                    <span
                      className={`status-badge ${u.enabled ? 'accepted' : 'rejected'}`}
                    >
                      {u.enabled ? 'Active' : 'Locked'}
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      className={`btn btn-sm ${u.enabled ? 'btn-danger' : 'btn-success'}`}
                      disabled={updatingId === u.userId}
                      onClick={() => handleStatusToggle(u.userId, u.enabled)}
                    >
                      {u.enabled ? 'Lock Account' : 'Unlock Account'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
