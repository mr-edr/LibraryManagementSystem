import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import CatalogView from './components/CatalogView';
import MyBooksView from './components/MyBooksView';
import LibrarianView from './components/LibrarianView';
import AdminView from './components/AdminView';
import AuthModal from './components/AuthModal';
import { CheckCircle, AlertCircle } from 'lucide-react';

function AppContent() {
  const [currentTab, setCurrentTab] = useState('catalog');
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [notification, setNotification] = useState(null);

  const { user, isLibrarian, isAdmin } = useAuth();

  // Redirect if unauthorized on protected tabs
  useEffect(() => {
    if (currentTab === 'my-books' && !user) {
      setCurrentTab('catalog');
    } else if (currentTab === 'librarian' && !isLibrarian) {
      setCurrentTab('catalog');
    } else if (currentTab === 'admin' && !isAdmin) {
      setCurrentTab('catalog');
    }
  }, [user, isLibrarian, isAdmin, currentTab]);

  const notify = (message, type = 'success') => {
    setNotification({ message, type });
    setTimeout(() => {
      setNotification(null);
    }, 4000);
  };

  return (
    <div className="app-container">
      <Navbar
        currentTab={currentTab}
        setCurrentTab={setCurrentTab}
        onOpenAuth={() => setAuthModalOpen(true)}
      />

      <main className="main-content">
        {currentTab === 'catalog' && (
          <CatalogView
            onNotify={notify}
            onOpenAuth={() => setAuthModalOpen(true)}
          />
        )}

        {currentTab === 'my-books' && user && (
          <MyBooksView onNotify={notify} />
        )}

        {currentTab === 'librarian' && isLibrarian && (
          <LibrarianView onNotify={notify} />
        )}

        {currentTab === 'admin' && isAdmin && (
          <AdminView onNotify={notify} />
        )}
      </main>

      <AuthModal
        isOpen={authModalOpen}
        onClose={() => setAuthModalOpen(false)}
        onSuccess={() => notify('Successfully authenticated!', 'success')}
      />

      {notification && (
        <div className={`alert-toast alert-${notification.type}`}>
          {notification.type === 'error' ? (
            <AlertCircle size={18} />
          ) : (
            <CheckCircle size={18} />
          )}
          <span>{notification.message}</span>
        </div>
      )}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
