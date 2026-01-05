import NotificationBell from "./NotificationBell";

export default function Navbar() {

  return (
    <nav className="bg-gray-900 text-white px-8 py-4 flex justify-between items-center shadow-lg fixed w-full top-0 z-50">
      <div className="flex items-center gap-6">
        <NotificationBell />
      </div>
    </nav>
  );
}