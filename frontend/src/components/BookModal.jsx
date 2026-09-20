import React, { useState, useEffect } from 'react';
import { api } from '../api';
import { X, BookPlus, Save } from 'lucide-react';

export default function BookModal({ isOpen, book, onClose, onSuccess }) {
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [stock, setStock] = useState(1);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const isEditing = Boolean(book);

  useEffect(() => {
    if (book) {
      setName(book.name || '');
      setCategory(book.category || '');
      setDescription(book.description || '');
      setStock(book.stock ?? 1);
    } else {
      setName('');
      setCategory('');
      setDescription('');
      setStock(1);
    }
    setError('');
  }, [book, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!name.trim()) throw new Error('Book name is required');
      if (!category.trim()) throw new Error('Category is required');
      if (stock < 0) throw new Error('Stock cannot be negative');

      const payload = {
        name: name.trim(),
        category: category.trim(),
        description: description.trim(),
        stock: parseInt(stock, 10),
      };

      if (isEditing) {
        await api.updateBook(book.id, payload);
      } else {
        await api.addBook(payload);
      }

      if (onSuccess) onSuccess();
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to save book');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{isEditing ? 'Edit Book' : 'Add New Book'}</h2>
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
          <div className="form-group">
            <label className="form-label">Book Title *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Clean Code"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Category *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Software Engineering"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Stock Count *</label>
            <input
              type="number"
              min="0"
              className="form-control"
              value={stock}
              onChange={(e) => setStock(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Description</label>
            <textarea
              className="form-control"
              rows="3"
              placeholder="Brief summary or description of the book..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            {isEditing ? <Save size={16} /> : <BookPlus size={16} />}
            <span>{loading ? 'Saving...' : isEditing ? 'Save Changes' : 'Add Book'}</span>
          </button>
        </form>
      </div>
    </div>
  );
}
