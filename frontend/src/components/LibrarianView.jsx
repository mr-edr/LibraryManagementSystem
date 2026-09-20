import React, { useState, useEffect } from 'react';
import { api } from '../api';
import {
  ClipboardList,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  BookCopy,
  Calendar,
  User as UserIcon,
  RefreshCw,
  Clock,
  Check,
  X
} from 'lucide-react';

export default function LibrarianView({ onNotify }) {
  const [activeTab, setActiveTab] = useState('requests'); // 'requests' | 'borrowed' | 'overdue'
  const [requests, setRequests] = useState([]);
  const [borrowedSummary, setBorrowedSummary] = useState([]);
  const [overdueLoans, setOverdueLoans] = useState([]);
  const [statusFilter, setStatusFilter] = useState('PENDING');
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);

  useEffect(() => {
    loadData();
  }, [activeTab, statusFilter]);

  const loadData = async () => {
    try {
      setLoading(true);
      if (activeTab === 'requests') {
        const data = await api.getLibrarianRequests(statusFilter);
        setRequests(data || []);
      } else if (activeTab === 'borrowed') {
        const data = await api.getBorrowedSummary();
        setBorrowedSummary(data || []);
      } else if (activeTab === 'overdue') {
        const data = await api.getOverdueLoans();
        setOverdueLoans(data || []);
      }
    } catch (err) {
      onNotify(err.message || 'Failed to load librarian data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (requestId) => {
    try {
      setProcessingId(`approve-${requestId}`);
      await api.approveRequest(requestId);
      onNotify(`Request #${requestId} approved successfully`, 'success');
      loadData();
    } catch (err) {
      onNotify(err.message || 'Approval failed', 'error');
    } finally {
      setProcessingId(null);
    }
  };

  const handleReject = async (requestId) => {
    if (!window.confirm(`Are you sure you want to reject request #${requestId}?`)) return;

    try {
      setProcessingId(`reject-${requestId}`);
      await api.rejectRequest(requestId);
      onNotify(`Request #${requestId} has been rejected`, 'success');
      loadData();
    } catch (err) {
      onNotify(err.message || 'Rejection failed', 'error');
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div>
      <div className="section-header">
        <div>
          <h1 className="section-title">Librarian Operations Desk</h1>
          <p className="section-subtitle">
            Review circulation requests, monitor active book loans, and audit overdue fines
          </p>
        </div>

        <button className="btn btn-outline" onClick={loadData} disabled={loading}>
          <RefreshCw size={15} className={loading ? 'animate-spin' : ''} />
          <span>Refresh</span>
        </button>
      </div>

      <div className="subtabs">
        <button
          className={`subtab-btn ${activeTab === 'requests' ? 'active' : ''}`}
          onClick={() => setActiveTab('requests')}
        >
          <ClipboardList size={16} />
          <span>Circulation Requests</span>
        </button>

        <button
          className={`subtab-btn ${activeTab === 'borrowed' ? 'active' : ''}`}
          onClick={() => setActiveTab('borrowed')}
        >
          <BookCopy size={16} />
          <span>Grouped Borrows Summary</span>
        </button>

        <button
          className={`subtab-btn ${activeTab === 'overdue' ? 'active' : ''}`}
          onClick={() => setActiveTab('overdue')}
        >
          <AlertTriangle size={16} />
          <span>Overdue & Fines Tracker</span>
        </button>
      </div>

      {activeTab === 'requests' && (
        <div style={{ marginBottom: '1rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <span style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--slate-600)' }}>
            Filter Status:
          </span>
          {['PENDING', 'ACCEPTED', 'REJECTED', ''].map((st) => (
            <button
              key={st}
              className={`btn btn-sm ${statusFilter === st ? 'btn-primary' : 'btn-outline'}`}
              onClick={() => setStatusFilter(st)}
            >
              {st || 'ALL'}
            </button>
          ))}
        </div>
      )}

      {loading ? (
        <div className="empty-state">
          <p>Fetching records...</p>
        </div>
      ) : activeTab === 'requests' ? (
        requests.length === 0 ? (
          <div className="empty-state">
            <CheckCircle2 size={48} />
            <p>No requests matching the selected filter.</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Req ID</th>
                  <th>Type</th>
                  <th>Book Title</th>
                  <th>Requester</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Review Actions</th>
                </tr>
              </thead>
              <tbody>
                {requests.map((req) => (
                  <tr key={req.requestId}>
                    <td>#{req.requestId}</td>
                    <td>
                      <strong>{req.type}</strong>
                    </td>
                    <td>{req.book?.name || 'N/A'}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                        <UserIcon size={14} className="text-slate-400" />
                        <span>{req.user?.name || req.user?.userName}</span>
                        <span style={{ fontSize: '0.75rem', color: 'var(--slate-400)' }}>
                          (@{req.user?.userName})
                        </span>
                      </div>
                    </td>
                    <td>{req.requestDate}</td>
                    <td>
                      <span className={`status-badge ${req.status?.toLowerCase()}`}>
                        {req.status}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      {req.status === 'PENDING' ? (
                        <div style={{ display: 'inline-flex', gap: '0.5rem' }}>
                          <button
                            className="btn btn-success btn-sm"
                            disabled={Boolean(processingId)}
                            onClick={() => handleApprove(req.requestId)}
                          >
                            <Check size={14} />
                            <span>Approve</span>
                          </button>
                          <button
                            className="btn btn-danger btn-sm"
                            disabled={Boolean(processingId)}
                            onClick={() => handleReject(req.requestId)}
                          >
                            <X size={14} />
                            <span>Reject</span>
                          </button>
                        </div>
                      ) : (
                        <span style={{ fontSize: '0.85rem', color: 'var(--slate-400)' }}>
                          Completed
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      ) : activeTab === 'borrowed' ? (
        borrowedSummary.length === 0 ? (
          <div className="empty-state">
            <BookCopy size={48} />
            <p>No active borrows currently in circulation.</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {borrowedSummary.map((item, idx) => (
              <div
                key={idx}
                style={{
                  background: 'white',
                  borderRadius: '8px',
                  border: '1px solid var(--slate-200)',
                  padding: '1.25rem',
                  boxShadow: 'var(--shadow-sm)',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: '700', color: 'var(--slate-900)' }}>
                      {item.book?.name}
                    </h3>
                    <span style={{ fontSize: '0.8rem', color: 'var(--slate-500)' }}>
                      Category: {item.book?.category} | Remaining Stock: {item.book?.stock}
                    </span>
                  </div>
                  <span
                    style={{
                      background: 'var(--primary-light)',
                      color: 'var(--primary)',
                      padding: '0.3rem 0.75rem',
                      borderRadius: '9999px',
                      fontSize: '0.825rem',
                      fontWeight: '700',
                      height: 'fit-content',
                    }}
                  >
                    {item.borrowerCount} Active {item.borrowerCount === 1 ? 'Borrower' : 'Borrowers'}
                  </span>
                </div>

                <div className="table-container" style={{ marginTop: '0.5rem', border: '1px solid var(--slate-100)' }}>
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Borrower Name</th>
                        <th>Username</th>
                        <th>Due Date</th>
                      </tr>
                    </thead>
                    <tbody>
                      {item.borrowers?.map((b, bIdx) => (
                        <tr key={bIdx}>
                          <td>{b.user?.name}</td>
                          <td>@{b.user?.userName}</td>
                          <td>
                            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                              <Calendar size={14} className="text-slate-400" />
                              {b.dueDate}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            ))}
          </div>
        )
      ) : overdueLoans.length === 0 ? (
        <div className="empty-state">
          <CheckCircle2 size={48} />
          <p>No overdue loans found! All borrowings are on schedule.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Loan ID</th>
                <th>Book Title</th>
                <th>Borrower</th>
                <th>Due Date</th>
                <th>Days Overdue</th>
                <th>Calculated Fine</th>
              </tr>
            </thead>
            <tbody>
              {overdueLoans.map((loan) => (
                <tr key={loan.transactionId}>
                  <td>#{loan.transactionId}</td>
                  <td>
                    <strong>{loan.book?.name}</strong>
                  </td>
                  <td>
                    {loan.borrower?.name} (@{loan.borrower?.userName})
                  </td>
                  <td style={{ color: 'var(--danger)', fontWeight: '600' }}>
                    {loan.dueDate}
                  </td>
                  <td>
                    <span className="status-badge overdue">
                      +{loan.overdueDays} days
                    </span>
                  </td>
                  <td style={{ fontWeight: '700', color: 'var(--danger)' }}>
                    ₹{loan.fineAmount?.toFixed(2)}
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
