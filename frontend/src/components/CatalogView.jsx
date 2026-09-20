import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { Search, Plus, BookOpen, Edit2, Trash2, Send, Check } from 'lucide-react';
import BookModal from './BookModal';

export default function CatalogView({ onNotify, onOpenAuth }) {
  const { user, isLibrarian } = useAuth();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedAvailability, setSelectedAvailability] = useState('');
  const [categories, setCategories] = useState([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [requestingBookId, setRequestingBookId] = useState(null);

  useEffect(() => {
    fetchBooks();
  }, [searchQuery, selectedCategory, selectedAvailability]);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      let data;
      if (searchQuery || selectedCategory || selectedAvailability) {
        data = await api.searchBooks(searchQuery, selectedCategory, selectedAvailability);
      } else {
        data = await api.getBooks();
      }
      setBooks(data);

      // Extract unique categories for filter dropdown
      const uniqueCats = Array.from(new Set(data.map((b) => b.category).filter(Boolean)));
      setCategories((prev) => Array.from(new Set([...prev, ...uniqueCats])));
    } catch (err) {
      onNotify(err.message || 'Failed to load books', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleBorrowRequest = async (book) => {
    if (!user) {
      onOpenAuth();
      return;
    }

    try {
      setRequestingBookId(book.id);
      await api.requestBorrow(book.id);
      onNotify(`Borrow request submitted for "${book.name}". Awaiting librarian approval.`, 'success');
    } catch (err) {
      onNotify(err.message || 'Borrow request failed', 'error');
    } finally {
      setRequestingBookId(null);
    }
  };

  const handleDeleteBook = async (book) => {
    if (!window.confirm(`Are you sure you want to delete "${book.name}"?`)) return;

    try {
      await api.deleteBook(book.id);
      onNotify(`Book "${book.name}" deleted successfully`, 'success');
      fetchBooks();
    } catch (err) {
      onNotify(err.message || 'Failed to delete book', 'error');
    }
  };

  return (
    <div>
      <div className="section-header">
        <div>
          <h1 className="section-title">Library Book Catalog</h1>
          <p className="section-subtitle">
            Explore our collection and request books with librarian-verified checkout
          </p>
        </div>
        {isLibrarian && (
          <button
            className="btn btn-primary"
            onClick={() => {
              setEditingBook(null);
              setModalOpen(true);
            }}
          >
            <Plus size={16} />
            <span>Add New Book</span>
          </button>
        )}
      </div>

      <div className="filter-bar">
        <div className="search-input-wrapper">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search by title or description..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <select
          className="filter-select"
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>

        <select
          className="filter-select"
          value={selectedAvailability}
          onChange={(e) => setSelectedAvailability(e.target.value)}
        >
          <option value="">All Availabilities</option>
          <option value="AVAILABLE">In Stock Only</option>
          <option value="NOT_AVAILABLE">Out of Stock</option>
        </select>
      </div>

      {loading ? (
        <div className="empty-state">
          <p>Loading library catalog...</p>
        </div>
      ) : books.length === 0 ? (
        <div className="empty-state">
          <BookOpen size={48} />
          <p>No books found matching your criteria.</p>
        </div>
      ) : (
        <div className="books-grid">
          {books.map((book) => {
            const isAvailable = book.availability === 'AVAILABLE' && book.stock > 0;
            return (
              <div key={book.id} className="book-card">
                <div>
                  <span className="book-category">{book.category}</span>
                  <h3 className="book-title">{book.name}</h3>
                  <p className="book-desc">{book.description || 'No description provided.'}</p>
                </div>

                <div>
                  <div className="book-meta">
                    <span className={`stock-badge ${isAvailable ? 'available' : 'unavailable'}`}>
                      {isAvailable ? `${book.stock} in stock` : 'Out of stock'}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--slate-400)' }}>
                      ID: #{book.id}
                    </span>
                  </div>

                  <div className="card-actions">
                    <button
                      className="btn btn-primary btn-sm"
                      style={{ flex: 1 }}
                      disabled={!isAvailable || requestingBookId === book.id}
                      onClick={() => handleBorrowRequest(book)}
                    >
                      <Send size={14} />
                      <span>{requestingBookId === book.id ? 'Requesting...' : 'Request Borrow'}</span>
                    </button>

                    {isLibrarian && (
                      <>
                        <button
                          className="btn btn-outline btn-sm"
                          title="Edit Book"
                          onClick={() => {
                            setEditingBook(book);
                            setModalOpen(true);
                          }}
                        >
                          <Edit2 size={14} />
                        </button>
                        <button
                          className="btn btn-outline btn-sm"
                          title="Delete Book"
                          style={{ color: 'var(--danger)' }}
                          onClick={() => handleDeleteBook(book)}
                        >
                          <Trash2 size={14} />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <BookModal
        isOpen={modalOpen}
        book={editingBook}
        onClose={() => setModalOpen(false)}
        onSuccess={() => {
          onNotify(editingBook ? 'Book updated successfully' : 'Book added to catalog', 'success');
          fetchBooks();
        }}
      />
    </div>
  );
}
