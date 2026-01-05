import React from "react";

interface PaginationProps {
    page: number;
    totalPages: number;
    onPageChange: (page: number) => void;
  }
  
  export default function Pagination({
    page,
    totalPages,
    onPageChange,
  }: PaginationProps) {
    return (
      <div className="pagination">
        <button onClick={() => onPageChange(page - 1)} disabled={page === 0}>
          Anterior
        </button>
  
        <span>
          Página {page + 1} de {totalPages}
        </span>
  
        <button
          onClick={() => onPageChange(page + 1)}
          disabled={page + 1 === totalPages}
        >
          Próxima
        </button>
      </div>
    );
  }