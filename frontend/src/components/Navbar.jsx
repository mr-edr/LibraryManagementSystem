import React from 'react';
import { useAuth } from '../context/AuthContext';
import { BookOpen, BookMarked, ClipboardList, ShieldCheck, LogIn, LogOut, User as UserIcon } from 'lucide-react';

export default function Navbar({ currentTab, setCurrentTab, onOpenAuth }) {
  const { user, logout, isLibrarian, isAdmin } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <div className="nav-brand" onClick={() => setCurrentTab('catalog')}>
          <BookOpen size={26} />
          <span>LibrarySystem</span>
        </div>

        <div className="nav-links">
          <button
            className={`nav-btn ${currentTab === 'catalog' ? 'active' : ''}`}
            onClick={() => setCurrentTab('catalog')}
          >
            <BookOpen size={16} />
            <span>Catalog</span>
          </button>

          {user && (
            <button
              className={`nav-btn ${currentTab === 'my-books' ? 'active' : ''}`}
              onClick={() => setCurrentTab('my-books')}
            >
              <BookMarked size={16} />
              <span>My Loans & Requests</span>
            </button>
          )}

          {isLibrarian && (
            <button
              className={`nav-btn ${currentTab === 'librarian' ? 'active' : ''}`}
              onClick={() => setCurrentTab('librarian')}
            >
              <ClipboardList size={16} />
              <span>Librarian Desk</span>
            </button>
          )}

          {isAdmin && (
            <button
              className={`nav-btn ${currentTab === 'admin' ? 'active' : ''}`}
              onClick={() => setCurrentTab('admin')}
            >
              <ShieldCheck size={16} />
              <span>Admin Console</span>
            </button>
          )}

          {user ? (
            <div className="nav-user">
              <div className="user-badge">
                <UserIcon size={16} className="text-slate-400" />
                <span>{user.name || user.userName}</span>
                <span className={`role-tag ${user.role?.toLowerCase()}`}>
                  {user.role}
                </span>
              </div>
              <button
                className="btn btn-outline btn-sm"
                onClick={logout}
                title="Log out"
              >
                <LogOut size={14} />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <button
              className="btn btn-primary btn-sm"
              onClick={onOpenAuth}
            >
              <LogIn size={15} />
              <span>Sign In / Register</span>
            </button>
          )}
        </div>
      </div>
    </nav>
  );
}
