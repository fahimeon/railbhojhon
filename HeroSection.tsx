import React from 'react';

const HeroSection = () => {
  return (
    <div className="min-h-screen bg-[#2563eb] flex flex-col items-center justify-center p-4 relative overflow-hidden font-sans">
      
      {/* Decorative Background Shapes */}
      <div className="absolute left-0 top-[45%] w-24 md:w-32 h-10 md:h-12 bg-[#1d4ed8] rounded-r-full hidden md:block"></div>
      <div className="absolute right-0 top-[30%] w-32 md:w-40 h-10 md:h-12 bg-[#1d4ed8] rounded-l-full hidden md:block"></div>
      <div className="absolute left-1/4 bottom-0 w-8 md:w-16 h-32 md:h-48 bg-[#1d4ed8] rounded-t-sm hidden md:block"></div>
      <div className="absolute right-1/4 bottom-0 w-8 md:w-16 h-40 md:h-64 bg-[#1d4ed8] rounded-t-sm hidden md:block"></div>

      {/* Header section */}
      <div className="text-center mb-8 md:mb-12 z-10 mt-8">
        <h1 className="text-4xl md:text-5xl lg:text-[54px] font-extrabold text-white mb-4 tracking-wide">
          Choose Your Role
        </h1>
        <p className="text-blue-200 text-lg md:text-xl font-medium tracking-wide">
          Select One Option to Continue
        </p>
      </div>

      {/* Main White Card Container */}
      <div className="bg-white rounded-[2rem] p-6 md:p-8 lg:p-12 shadow-2xl z-10 w-full max-w-6xl mb-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 md:gap-8">
          
          {/* Card 1: Restaurant Owner */}
          <div className="bg-[#f0f6ff] rounded-[2rem] px-6 py-10 flex flex-col items-center text-center group hover:bg-[#e4efff] hover:-translate-y-2 hover:shadow-xl hover:shadow-blue-200 transition-all duration-300 cursor-pointer">
            <div className="w-32 h-32 md:w-40 md:h-40 mb-6 flex items-center justify-center transform group-hover:scale-110 transition-transform duration-500 ease-out">
              <img 
                src="restaurantowner icon.png" 
                alt="Restaurant Owner" 
                className="w-full h-full object-contain"
              />
            </div>
            <h2 className="text-2xl md:text-[28px] font-extrabold text-[#1f2937] mb-3 leading-tight group-hover:text-[#2563eb] transition-colors">
              Restaurant<br />Owner
            </h2>
            <p className="text-sm md:text-[15px] text-gray-500 leading-relaxed px-2 font-medium">
              Get Extra Orders from<br />our App!
            </p>
          </div>

          {/* Card 2: Passenger */}
          <div className="bg-[#f0f6ff] rounded-[2rem] px-6 py-10 flex flex-col items-center text-center group hover:bg-[#e4efff] hover:-translate-y-2 hover:shadow-xl hover:shadow-blue-200 transition-all duration-300 cursor-pointer">
            <div className="w-32 h-32 md:w-40 md:h-40 mb-6 flex items-center justify-center transform group-hover:scale-110 transition-transform duration-500 ease-out">
              <img 
                src="passenger icon.png" 
                alt="Passenger" 
                className="w-full h-full object-contain"
              />
            </div>
            <h2 className="text-2xl md:text-[28px] font-extrabold text-[#1f2937] mb-3 leading-tight group-hover:text-[#2563eb] transition-colors">
              Passenger
            </h2>
            <p className="text-sm md:text-[15px] text-gray-500 leading-relaxed px-2 font-medium mt-auto">
              Order Your Favourite Food<br />from nearby restaurants
            </p>
          </div>

          {/* Card 3: Admin */}
          <div className="bg-[#f0f6ff] rounded-[2rem] px-6 py-10 flex flex-col items-center text-center group hover:bg-[#e4efff] hover:-translate-y-2 hover:shadow-xl hover:shadow-blue-200 transition-all duration-300 cursor-pointer">
            <div className="w-32 h-32 md:w-40 md:h-40 mb-6 flex items-center justify-center transform group-hover:scale-110 transition-transform duration-500 ease-out">
              <img 
                src="admin icon.png" 
                alt="Admin" 
                className="w-full h-full object-contain"
              />
            </div>
            <h2 className="text-2xl md:text-[28px] font-extrabold text-[#1f2937] mb-3 leading-tight group-hover:text-[#2563eb] transition-colors">
              Admin
            </h2>
            <p className="text-sm md:text-[15px] text-gray-500 leading-relaxed px-2 font-medium mt-auto">
              Get to see dashboards<br />and Insights
            </p>
          </div>

        </div>
      </div>
    </div>
  );
};

export default HeroSection;
