#include <boost/container/vector.hpp>
#include <boost/interprocess/allocators/allocator.hpp>
#include <boost/interprocess/managed_shared_memory.hpp>

#include <iostream>

#include "data.h"

int main ()
{
   using namespace boost::interprocess;

   managed_shared_memory segment(open_read_only,"MyName");

   //Alias an STL-like allocator of ints that allocates ints from the segment
   typedef allocator<sdata_t, managed_shared_memory::segment_manager>
      ShmemAllocator;

   //Alias a vector that uses the previous STL-like allocator
   typedef boost::container::vector<sdata_t, ShmemAllocator> MyVector;

   MyVector *myvector = segment.find<MyVector>("MyVector").first;

   for(auto& elem: *myvector)
   {
      std::cout << elem.s << ", " << elem.n << std::endl;
   }
   
   return 0;
}