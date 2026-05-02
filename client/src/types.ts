export type Role = 'ADMIN' | 'LIBRARIAN' | 'CLERK' | 'BORROWER'

export type Book = {
  id: number
  isbn: string
  title: string
  author: string
  subject: string
  issued: boolean
  totalCopies: number
  availableCopies: number
}

export type Borrower = {
  id: number
  name: string
  address: string
  phoneNumber: string
  borrowedBooksCount: number
  onHoldBooksCount: number
}

export type User = {
  userId: number
  username: string
  fullName: string
  role: Role
}

export type ManagedUser = {
  id: number
  username: string
  fullName: string
  role: Role
  active: boolean
}

export type ActiveLoan = {
  loanId: number
  borrowerId: number
  bookId: number
  bookTitle: string
  dueDate: string
  pendingFine: number
  finePaid: boolean
}

export type HoldItem = {
  id: number
  bookId: number
  bookTitle: string
  borrowerName: string
  requestDate: string
  status: string
}

export type DashboardStats = {
  totalBooks: number
  activeLoans: number
  overdueUnpaidLoans: number
  activeHolds: number
  totalBorrowers: number
  totalStaff: number
}

export type LoanHistory = {
  loanId: number
  borrowerId: number
  bookId: number
  bookTitle: string
  issueDate: string
  dueDate: string
  returnDate: string | null
  finePaid: boolean
}
