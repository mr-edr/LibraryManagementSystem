import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { X, LogIn, UserPlus } from 'lucide-react';

export default function AuthModal({ isOpen, onClose, onSuccess }) {
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName] = useState('');
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login, register } = useAuth();

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isRegister) {
        if (!name.trim()) throw new Error('Please enter your full name');
        if (!userName.trim()) throw new Error('Please enter a username');
        if (!password.trim()) throw new Error('Please enter a password');
        await register(name.trim(), userName.trim(), password);
      } else {
        if (!userName.trim() || !password.trim()) throw new Error('Please enter your username and password');
        await login(userName.trim(), password);
      }
      if (onSuccess) onSuccess();
      onClose();
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">
            {isRegister ? 'Create an Account' : 'Sign In to Library'}
          </h2>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {error && (
          <div style={{ padding: '0.75rem', backgroundColor: 'var(--danger-light)', color: 'var(--danger)', borderRadius: '6px', marginBottom: '1rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. Alice Smith"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. alicesmith"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            {isRegister ? <UserPlus size={16} /> : <LogIn size={16} />}
            <span>{loading ? 'Processing...' : isRegister ? 'Register Account' : 'Sign In'}</span>
          </button>
        </form>

        <div style={{ marginTop: '1.25rem', textAlign: 'center', fontSize: '0.875rem', color: 'var(--slate-600)' }}>
          {isRegister ? (
            <>
              Already have an account?{' '}
              <a
                href="#login"
                style={{ color: 'var(--primary)', fontWeight: '600', textDecoration: 'none' }}
                onClick={(e) => {
                  e.preventDefault();
                  setIsRegister(false);
                  setError('');
                }}
              >
                Sign In
              </a>
            </>
          ) : (
            <>
              Don't have an account?{' '}
              <a
                href="#register"
                style={{ color: 'var(--primary)', fontWeight: '600', textDecoration: 'none' }}
                onClick={(e) => {
                  e.preventDefault();
                  setIsRegister(true);
                  setError('');
                }}
              >
                Register Now
              </a>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
