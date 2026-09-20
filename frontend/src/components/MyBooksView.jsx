import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { BookMarked, Clock, RotateCcw, Calendar, AlertCircle, CheckCircle, XCircle } from 'lucide-react';

export default function MyBooksView({ onNotify }) {
  const [activeTab, setActiveTab] = useState('borrows'); // 'borrows' | 'requests'
  const [myBooks, setMyBooks] = useState([]);
  const [myRequests, setMyRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionInProgress, setActionInProgress] = useState(null);

  useEffect(() => {
    loadData();
  }, [activeTab]);

  const loadData = async () => {
    try {
      setLoading(true);
      if (activeTab === 'borrows') {
        const books = await api.getMyBooks();
        setMyBooks(books || []);
      } else {
        const requests = await api.getMyRequests();
        setMyRequests(requests || []);
      }
    } catch (err) {
      onNotify(err.message || 'Failed to load user data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestExtend = async (loan) => {
    try {
      setActionInProgress(`extend-${loan.transactionId}`);
      await api.requestExtend(loan.transactionId);
      onNotify(`Extension requested for "${loan.book?.name}". Awaiting approval.`, 'success');
      loadData();
    } catch (err) {
      onNotify(err.message || 'Failed to request extension', 'error');
    } finally {
      setActionInProgress(null);
    }
  };

  const handleRequestReturn = async (loan) => {
    try {
      setActionInProgress(`return-${loan.transactionId}`);
      await api.requestReturn(loan.transactionId);
      onNotify(`Return request submitted for "${loan.book?.name}". Awaiting librarian confirmation.`, 'success');
      loadData();
    } catch (err) {
      onNotify(err.message || 'Failed to request return', 'error');
    } finally {
      setActionInProgress(null);
    }
  };

  return (
    <div>
      <div className="section-header">
        <div>
          <h1 className="section-title">My Loans & Requests</h1>
          <p className="section-subtitle">
            Manage your borrowed books, track due dates, and view request approval status
          </p>
        </div>
      </div>

      <div className="subtabs">
        <button
          className={`subtab-btn ${activeTab === 'borrows' ? 'active' : ''}`}
          onClick={() => setActiveTab('borrows')}
        >
          <BookMarked size={16} />
          <span>Active Loans ({myBooks.length})</span>
        </button>

        <button
          className={`subtab-btn ${activeTab === 'requests' ? 'active' : ''}`}
          onClick={() => setActiveTab('requests')}
        >
          <Clock size={16} />
          <span>Request History ({myRequests.length})</span>
        </button>
      </div>

      {loading ? (
        <div className="empty-state">
          <p>Loading your data...</p>
        </div>
      ) : activeTab === 'borrows' ? (
        myBooks.length === 0 ? (
          <div className="empty-state">
            <BookMarked size={48} />
            <p>You have no active borrowed books right now.</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Loan ID</th>
                  <th>Book Title</th>
                  <th>Category</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {myBooks.map((loan) => {
                  const today = new Date().toISOString().split('T')[0];
                  const isOverdue = loan.dueDate && loan.dueDate < today;

                  return (
                    <tr key={loan.transactionId}>
                      <td>#{loan.transactionId}</td>
                      <td>
                        <strong>{loan.book?.name}</strong>
                      </td>
                      <td>{loan.book?.category}</td>
                      <td>
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                          <Calendar size={14} className="text-slate-400" />
                          {loan.dueDate || 'N/A'}
                        </span>
                      </td>
                      <td>
                        <span className={`status-badge ${isOverdue ? 'overdue' : 'borrowed'}`}>
                          {isOverdue ? 'OVERDUE' : loan.borrowed}
                        </span>
                      </td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.5rem' }}>
                          <button
                            className="btn btn-outline btn-sm"
                            disabled={actionInProgress === `extend-${loan.transactionId}`}
                            onClick={() => handleRequestExtend(loan)}
                            title="Request 14-day due date extension"
                          >
                            <Clock size={14} />
                            <span>Extend (+14d)</span>
                          </button>
                          <button
                            className="btn btn-primary btn-sm"
                            disabled={actionInProgress === `return-${loan.transactionId}`}
                            onClick={() => handleRequestReturn(loan)}
                            title="Initiate return"
                          >
                            <RotateCcw size={14} />
                            <span>Return</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )
      ) : myRequests.length === 0 ? (
        <div className="empty-state">
          <Clock size={48} />
          <p>No transaction requests on record.</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Req ID</th>
                <th>Type</th>
                <th>Book Title</th>
                <th>Request Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {myRequests.map((req) => (
                <tr key={req.requestId}>
                  <td>#{req.requestId}</td>
                  <td>
                    <strong>{req.type}</strong>
                  </td>
                  <td>{req.book?.name || 'N/A'}</td>
                  <td>{req.requestDate}</td>
                  <td>
                    <span className={`status-badge ${req.status?.toLowerCase()}`}>
                      {req.status === 'ACCEPTED' && <CheckCircle size={12} />}
                      {req.status === 'REJECTED' && <XCircle size={12} />}
                      {req.status === 'PENDING' && <Clock size={12} />}
                      <span>{req.status}</span>
                    </span>
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
